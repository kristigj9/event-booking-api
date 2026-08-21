package com.lhind.event_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_venue_row_number",
                        columnNames = {
                                "venue_id",
                                "seat_row",
                                "seat_number"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "seat_row",
            nullable = false
    )
    private String rowNumber;

    @Column(
            name = "seat_number",
            nullable = false
    )
    private Integer seatNumber;

    // Nje venue mund te kete disa seats
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "venue_id",
            nullable = false
    )
    private Venue venue;

    // Nje seat mund te perdoret ne disa event seats
    @OneToMany(mappedBy = "seat")
    @Builder.Default
    private List<EventSeat> eventSeats = new ArrayList<>();
}