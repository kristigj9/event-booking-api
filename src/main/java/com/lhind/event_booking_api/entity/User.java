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
    @OneToMany(mappedBy = "user_id")
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    //Lidhja One To Many, nje user disa Booking
    @OneToMany(
            mappedBy = "user_id")
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();
    //Lidhja nje user orgeniser me shume evente
    @OneToMany(mappedBy = "organizer_id")
    @Builder.Default
    private List<Event> organizedEvents = new ArrayList<>();
}