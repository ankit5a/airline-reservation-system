package com.airline.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "flights")
@NamedQueries({
        @NamedQuery(
                name = "Flight.findAll",
                query = "SELECT f FROM Flight f ORDER BY f.departureTime ASC"
        ),
        @NamedQuery(
                name = "Flight.findByFlightNumber",
                query = "SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber"
        ),
        @NamedQuery(
                name = "Flight.findByStatus",
                query = "SELECT f FROM Flight f WHERE f.status = :status ORDER BY f.departureTime ASC"
        ),
        @NamedQuery(
                name = "Flight.findByOriginAndDestination",
                query = "SELECT f FROM Flight f WHERE LOWER(f.origin) = LOWER(:origin) " +
                        "AND LOWER(f.destination) = LOWER(:destination) " +
                        "AND f.status = 'SCHEDULED' ORDER BY f.departureTime ASC"
        ),
        @NamedQuery(
                name = "Flight.findByOriginOnly",
                query = "SELECT f FROM Flight f WHERE LOWER(f.origin) = LOWER(:origin) " +
                        "AND f.status = 'SCHEDULED' ORDER BY f.departureTime ASC"
        ),
        @NamedQuery(
                name = "Flight.findByDestinationOnly",
                query = "SELECT f FROM Flight f WHERE LOWER(f.destination) = LOWER(:destination) " +
                        "AND f.status = 'SCHEDULED' ORDER BY f.departureTime ASC"
        ),
        @NamedQuery(
                name = "Flight.countByFlightNumber",
                query = "SELECT COUNT(f) FROM Flight f WHERE f.flightNumber = :flightNumber"
        )
})
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true)
    private String flightNumber;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "departure_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date departureTime;

    @Column(name = "arrival_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date arrivalTime;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "status", nullable = false)
    private String status;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public Date getDepartureTime() { return departureTime; }
    public void setDepartureTime(Date departureTime) { this.departureTime = departureTime; }

    public Date getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Date arrivalTime) { this.arrivalTime = arrivalTime; }

    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }

    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
