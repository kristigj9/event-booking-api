package com.lhind.event_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "booking_seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_booking_event_seat",
                        columnNames = {
                                "booking_id",
                                "event_seat_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nje booking mund te kete disa booking seats
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "booking_id",
            nullable = false
    )
    private Booking booking;

    // Nje event seat mund te lidhet me booking seat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "event_seat_id",
            nullable = false
    )
    private EventSeat eventSeat;
}