package com.airline.web.dtos;

public class BookingDto {
    private Long id;
    private String bookingReference;
    private Long flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private PassengerDto passenger;
    private String seatNumber;
    private String status;
    private String bookedAt;
    private Double totalPrice;

    // For create request
    private Long passengerId;
    private PassengerDto newPassenger;

    public BookingDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public PassengerDto getPassenger() { return passenger; }
    public void setPassenger(PassengerDto passenger) { this.passenger = passenger; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookedAt() { return bookedAt; }
    public void setBookedAt(String bookedAt) { this.bookedAt = bookedAt; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public PassengerDto getNewPassenger() { return newPassenger; }
    public void setNewPassenger(PassengerDto newPassenger) { this.newPassenger = newPassenger; }
}