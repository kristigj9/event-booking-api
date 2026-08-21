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
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    // Nje user mund te kete disa bookings
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    // Nje event mund te kete disa bookings
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "event_id",
            nullable = false
    )
    private Event event;

    // Gjeneron automatikisht bookingDate
    @PrePersist
    public void prePersist() {

        if (this.bookingDate == null) {
            this.bookingDate = LocalDateTime.now();
        }

        if (this.bookingStatus == null) {
            this.bookingStatus = BookingStatus.PENDING;
        }
    }

    // Nje booking mund te kete disa booking seats
    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    // Nje booking ka nje payment
    @OneToOne(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Payment payment;

    // Nje booking mund te lidhet me disa notifications
    @OneToMany(mappedBy = "booking")
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}