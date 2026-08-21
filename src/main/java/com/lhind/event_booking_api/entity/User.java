package com.lhind.event_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Nje user mund te kete disa reviews
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    // Nje user mund te kete disa bookings
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Nje organizer mund te kete disa events
    @OneToMany(mappedBy = "organizer")
    @Builder.Default
    private List<Event> organizedEvents = new ArrayList<>();

    // Nje user mund te kete disa waitlist entries
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Waitlist> waitlists = new ArrayList<>();

    // Nje user mund te kete disa notifications
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}