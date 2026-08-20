package com.lhind.event_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, updatable = false)
    private LocalDateTime bookingDate;
    @Column(nullable = false)
    private Integer seatsBooked;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus bookingStatus=BookingStatus.PENDING;//Sapo krijohet nje booking kalon ne PENDING

    //Lidhja me User Many to One, Disa Booking nga nje user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //Lidhja disa Booking nje event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    //Gjenerimi i bookingDate Automatike
    @PrePersist
    public void prePersist() {
        this.bookingDate = LocalDateTime.now();
    }

    //Lidhja e Booking me BookingSeat
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<BookingSeat> bookingSeats = new ArrayList<>();
    //Lidhja e Booking me Payment

    @OneToOne(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Payment payment;

}
