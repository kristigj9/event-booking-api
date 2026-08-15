package com.lhind.event_booking_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String eventName;
    @Column(length = 1000)
    private String description;
    @Column(nullable = false)
    private LocalDateTime startDateTime;
    @Column(nullable = false)
    private LocalDateTime endDateTime;
    @Column(nullable = false)
    private Integer totalSeats;
    @Column(nullable = false)
    private Integer availableSeats;
    @Column(nullable = false)
    private BigDecimal ticketPrice;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus eventStatus;
    //Lidhja nje event ka disa booking
    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Booking> bookings = new ArrayList<>();

    //Lidhja nje event ka disa kategori
    @OneToMany(
            mappedBy = "event_ctegory",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Category> categories = new ArrayList<>();

    //Lidhja Shume evente krijohen vetem nga nje organize

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    //Lidhja shume evente mund te jene ne disa venue

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;
}