package com.airline.facade;

import com.airline.entities.Flight;
import com.airline.exceptions.FlightNotFoundException;
import com.airline.exceptions.InvalidRequestException;
import com.airline.web.dtos.FlightDto;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class FlightFacadeImpl implements FlightFacade {

    @Inject
    private Provider<EntityManager> entityManagerProvider;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public List<FlightDto> getAllFlights() {
        EntityManager em = entityManagerProvider.get();

        // Named query defined on Flight entity
        return em.createNamedQuery("Flight.findAll", Flight.class)
                .getResultList()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FlightDto getFlightById(Long id) {
        EntityManager em = entityManagerProvider.get();
        Flight flight = em.find(Flight.class, id);
        if (flight == null) throw new FlightNotFoundException(id);
        return toDto(flight);
    }

    @Override
    public List<FlightDto> searchFlights(String origin, String destination, String date) {
        EntityManager em = entityManagerProvider.get();

        // Pick the right named query based on which params are present
        List<Flight> flights;

        boolean hasOrigin = origin != null && !origin.isEmpty();
        boolean hasDestination = destination != null && !destination.isEmpty();

        if (hasOrigin && hasDestination) {
            flights = em.createNamedQuery("Flight.findByOriginAndDestination", Flight.class)
                    .setParameter("origin", origin)
                    .setParameter("destination", destination)
                    .getResultList();

        } else if (hasOrigin) {
            flights = em.createNamedQuery("Flight.findByOriginOnly", Flight.class)
                    .setParameter("origin", origin)
                    .getResultList();

        } else if (hasDestination) {
            flights = em.createNamedQuery("Flight.findByDestinationOnly", Flight.class)
                    .setParameter("destination", destination)
                    .getResultList();

        } else {
            // No filters — return all scheduled
            flights = em.createNamedQuery("Flight.findByStatus", Flight.class)
                    .setParameter("status", "SCHEDULED")
                    .getResultList();
        }

        return flights.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public FlightDto createFlight(FlightDto flightDto) {
        if (flightDto.getFlightNumber() == null || flightDto.getFlightNumber().isEmpty()) {
            throw new InvalidRequestException("Flight number is required");
        }

        EntityManager em = entityManagerProvider.get();

        // Check duplicate flight number using named query
        Long count = em.createNamedQuery("Flight.countByFlightNumber", Long.class)
                .setParameter("flightNumber", flightDto.getFlightNumber())
                .getSingleResult();
        if (count > 0) {
            throw new InvalidRequestException("Flight number already exists: " + flightDto.getFlightNumber());
        }

        Flight flight = new Flight();
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setOrigin(flightDto.getOrigin());
        flight.setDestination(flightDto.getDestination());
        flight.setDepartureTime(Timestamp.valueOf(LocalDateTime.parse(flightDto.getDepartureTime(), FORMATTER)));
        flight.setArrivalTime(Timestamp.valueOf(LocalDateTime.parse(flightDto.getArrivalTime(), FORMATTER)));
        flight.setTotalSeats(flightDto.getTotalSeats());
        flight.setAvailableSeats(flightDto.getTotalSeats());
        flight.setPrice(flightDto.getPrice());
        flight.setStatus("SCHEDULED");

        em.getTransaction().begin();
        em.persist(flight);
        em.getTransaction().commit();

        return toDto(flight);
    }

    @Override
    public FlightDto updateFlight(Long id, FlightDto flightDto) {
        EntityManager em = entityManagerProvider.get();
        Flight flight = em.find(Flight.class, id);
        if (flight == null) throw new FlightNotFoundException(id);

        if (flightDto.getOrigin() != null) flight.setOrigin(flightDto.getOrigin());
        if (flightDto.getDestination() != null) flight.setDestination(flightDto.getDestination());
        if (flightDto.getPrice() != null) flight.setPrice(flightDto.getPrice());
        if (flightDto.getStatus() != null) flight.setStatus(flightDto.getStatus());
        if (flightDto.getAvailableSeats() != null) flight.setAvailableSeats(flightDto.getAvailableSeats());

        em.getTransaction().begin();
        em.merge(flight);
        em.getTransaction().commit();

        return toDto(flight);
    }

    @Override
    public void deleteFlight(Long id) {
        EntityManager em = entityManagerProvider.get();
        Flight flight = em.find(Flight.class, id);
        if (flight == null) throw new FlightNotFoundException(id);

        em.getTransaction().begin();
        em.remove(flight);
        em.getTransaction().commit();
    }

    private FlightDto toDto(Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setOrigin(flight.getOrigin());
        dto.setDestination(flight.getDestination());
        dto.setDepartureTime(flight.getDepartureTime().toString());
        dto.setArrivalTime(flight.getArrivalTime().toString());
        dto.setTotalSeats(flight.getTotalSeats());
        dto.setAvailableSeats(flight.getAvailableSeats());
        dto.setPrice(flight.getPrice());
        dto.setStatus(flight.getStatus());
        return dto;
    }
}
