package com.airline.entities;

import javax.persistence.*;

@Entity
@Table(name = "passengers")
@NamedQueries({
        @NamedQuery(
                name = "Passenger.findByPassportNumber",
                query = "SELECT p FROM Passenger p WHERE p.passportNumber = :passportNumber"
        ),
        @NamedQuery(
                name = "Passenger.findByEmail",
                query = "SELECT p FROM Passenger p WHERE p.email = :email"
        ),
        @NamedQuery(
                name = "Passenger.countByPassportNumber",
                query = "SELECT COUNT(p) FROM Passenger p WHERE p.passportNumber = :passportNumber"
        ),
        @NamedQuery(
                name = "Passenger.findAll",
                query = "SELECT p FROM Passenger p ORDER BY p.lastName ASC"
        )
})
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "passport_number", nullable = false, unique = true)
    private String passportNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}