package com.lhind.event_booking_api.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "event_seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_event_seat",
                        columnNames = {"event_id", "seat_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Lidhje e disa eventSeat me nje event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    //Lidhja e disa eventSeat me nje Seat fizik
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusSeat statusSeat = StatusSeat.AVAILABLE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal priceSeat;

    //Lidhja e EventSeat me BookingSeat
    @OneToMany(mappedBy = "eventSeat")
    @Builder.Default
    private List<BookingSeat> bookingSeats = new ArrayList<>();
}