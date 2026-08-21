package com.lhind.event_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

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

    // Nje event mund te kete disa bookings
    @OneToMany(mappedBy = "event")
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    // Nje event mund te kete disa kategori
    @ManyToMany
    @JoinTable(
            name = "event_categories",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    // Nje organizer mund te krijoje disa events
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "organizer_id",
            nullable = false
    )
    private User organizer;

    // Nje venue mund te kete disa events
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "venue_id",
            nullable = false
    )
    private Venue venue;

    // Nje event mund te kete disa event seats
    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<EventSeat> eventSeats = new ArrayList<>();

    // Nje event mund te kete disa waitlist entries
    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Waitlist> waitlists = new ArrayList<>();

    // Nje event mund te lidhet me disa notifications
    @OneToMany(mappedBy = "event")
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();
}