package com.airline.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bookings")
@NamedQueries({
        @NamedQuery(
                name = "Booking.findAll",
                query = "SELECT b FROM Booking b ORDER BY b.bookedAt DESC"
        ),
        @NamedQuery(
                name = "Booking.findByUserId",
                query = "SELECT b FROM Booking b WHERE b.user.id = :userId ORDER BY b.bookedAt DESC"
        ),
        @NamedQuery(
                name = "Booking.findByBookingReference",
                query = "SELECT b FROM Booking b WHERE b.bookingReference = :bookingReference"
        ),
        @NamedQuery(
                name = "Booking.findByFlightId",
                query = "SELECT b FROM Booking b WHERE b.flight.id = :flightId ORDER BY b.bookedAt DESC"
        ),
        @NamedQuery(
                name = "Booking.findByUserIdAndStatus",
                query = "SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = :status ORDER BY b.bookedAt DESC"
        ),
        @NamedQuery(
                name = "Booking.findByFlightIdAndPassengerId",
                query = "SELECT b FROM Booking b WHERE b.flight.id = :flightId AND b.passenger.id = :passengerId"
        ),
        @NamedQuery(
                name = "Booking.countConfirmedByFlightId",
                query = "SELECT COUNT(b) FROM Booking b WHERE b.flight.id = :flightId AND b.status = 'CONFIRMED'"
        )
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "booked_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bookedAt;

    @Column(name = "total_price")
    private Double totalPrice;

    @PrePersist
    public void prePersist() {
        this.bookedAt = new Date();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getBookedAt() { return bookedAt; }
    public void setBookedAt(Date bookedAt) { this.bookedAt = bookedAt; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}
