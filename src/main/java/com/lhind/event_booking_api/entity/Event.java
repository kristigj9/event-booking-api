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
    private String eventDescription;
    @Column(nullable = false)
    private LocalDateTime eventStartDateTime;
    @Column(nullable = false)
    private LocalDateTime eventEndDateTime;
    @Column(nullable = false)
    private Integer eventTotalSeats;
    @Column(nullable = false)
    private Integer eventAvailableSeats;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus eventStatus;

    //Lidhja nje event ka disa booking
    @OneToMany(
            mappedBy = "event"
    )
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    //Lidhja disa evente ka disa kategori
    @ManyToMany
    @JoinTable(
            name = "event_categories",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    //Lidhja Shume evente krijohen vetem nga nje organize

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    //Lidhja shume evente mund te jene ne disa venue

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    //Lidhja e nje event me disa eventSeat
    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<EventSeat> eventSeats = new ArrayList<>();

    // Lidhja e Event me Waitlist
    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Waitlist> waitlists = new ArrayList<>();

    //Lidhja Nje Event Disa Notification
    @OneToMany(mappedBy = "event")
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
    }