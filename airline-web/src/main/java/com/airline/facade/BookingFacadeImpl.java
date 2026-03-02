package com.airline.facade;

import com.airline.entities.Booking;
import com.airline.entities.Flight;
import com.airline.entities.Passenger;
import com.airline.entities.User;
import com.airline.exceptions.BookingNotFoundException;
import com.airline.exceptions.FlightNotFoundException;
import com.airline.exceptions.InvalidRequestException;
import com.airline.web.dtos.BookingDto;
import com.airline.web.dtos.PassengerDto;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class BookingFacadeImpl implements BookingFacade {

    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long userId) {
        EntityManager em = entityManagerProvider.get();

        Flight flight = em.find(Flight.class, bookingDto.getFlightId());
        if (flight == null) throw new FlightNotFoundException(bookingDto.getFlightId());
        if (flight.getAvailableSeats() <= 0) {
            throw new InvalidRequestException("No seats available on this flight");
        }

        User user = em.find(User.class, userId);
        if (user == null) throw new InvalidRequestException("User not found");

        Passenger passenger = resolvePassenger(em, bookingDto);

        em.getTransaction().begin();

        if (passenger.getId() == null) {
            em.persist(passenger);
            em.flush(); // so passenger.getId() is populated before Booking is persisted
        }

        Booking booking = new Booking();
        booking.setBookingReference("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        booking.setFlight(flight);
        booking.setPassenger(passenger);
        booking.setUser(user);
        booking.setSeatNumber(generateSeatNumber(flight));
        booking.setStatus("CONFIRMED");
        booking.setTotalPrice(flight.getPrice());

        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        em.merge(flight);
        em.persist(booking);

        em.getTransaction().commit();

        return toDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long id) {
        EntityManager em = entityManagerProvider.get();
        Booking booking = em.find(Booking.class, id);
        if (booking == null) throw new BookingNotFoundException(id);
        return toDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookings() {
        EntityManager em = entityManagerProvider.get();

        // Named query defined on Booking entity
        return em.createNamedQuery("Booking.findAll", Booking.class)
                .getResultList()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByUserId(Long userId) {
        EntityManager em = entityManagerProvider.get();

        // Named query filters by userId
        return em.createNamedQuery("Booking.findByUserId", Booking.class)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelBooking(Long id) {
        EntityManager em = entityManagerProvider.get();
        Booking booking = em.find(Booking.class, id);
        if (booking == null) throw new BookingNotFoundException(id);

        em.getTransaction().begin();
        booking.setStatus("CANCELLED");

        // Restore available seat on the flight
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + 1);
        em.merge(flight);
        em.merge(booking);

        em.getTransaction().commit();
    }

    // -------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------

    /**
     * Returns an existing Passenger (by ID or passport lookup)
     * or builds a new unsaved Passenger from the DTO.
     */
    private Passenger resolvePassenger(EntityManager em, BookingDto bookingDto) {
        if (bookingDto.getPassengerId() != null) {
            Passenger existing = em.find(Passenger.class, bookingDto.getPassengerId());
            if (existing == null) throw new InvalidRequestException("Passenger not found with id: " + bookingDto.getPassengerId());
            return existing;
        }

        if (bookingDto.getNewPassenger() == null) {
            throw new InvalidRequestException("Either passengerId or newPassenger details are required");
        }

        PassengerDto p = bookingDto.getNewPassenger();

        // Check if a passenger with this passport already exists
        try {
            return em.createNamedQuery("Passenger.findByPassportNumber", Passenger.class)
                    .setParameter("passportNumber", p.getPassportNumber())
                    .getSingleResult();
        } catch (NoResultException e) {
            // No existing passenger — build a new one (not yet persisted)
            Passenger newPassenger = new Passenger();
            newPassenger.setFirstName(p.getFirstName());
            newPassenger.setLastName(p.getLastName());
            newPassenger.setPassportNumber(p.getPassportNumber());
            newPassenger.setEmail(p.getEmail());
            newPassenger.setPhone(p.getPhone());
            return newPassenger;
        }
    }

    private String generateSeatNumber(Flight flight) {
        int occupied = flight.getTotalSeats() - flight.getAvailableSeats();
        char row = (char) ('A' + (occupied / 6));
        int col = (occupied % 6) + 1;
        return "" + row + col;
    }

    private BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        dto.setFlightId(booking.getFlight().getId());
        dto.setFlightNumber(booking.getFlight().getFlightNumber());
        dto.setOrigin(booking.getFlight().getOrigin());
        dto.setDestination(booking.getFlight().getDestination());
        dto.setSeatNumber(booking.getSeatNumber());
        dto.setStatus(booking.getStatus());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setBookedAt(booking.getBookedAt() != null ? booking.getBookedAt().toString() : null);

        PassengerDto p = new PassengerDto();
        p.setId(booking.getPassenger().getId());
        p.setFirstName(booking.getPassenger().getFirstName());
        p.setLastName(booking.getPassenger().getLastName());
        p.setPassportNumber(booking.getPassenger().getPassportNumber());
        p.setEmail(booking.getPassenger().getEmail());
        p.setPhone(booking.getPassenger().getPhone());
        dto.setPassenger(p);

        return dto;
    }
}