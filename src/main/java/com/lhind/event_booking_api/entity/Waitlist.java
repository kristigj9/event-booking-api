package com.lhind.event_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "waitlists",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_waitlist_user_event",
                        columnNames = {"user_id", "event_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Numri i vendeve qe user-i kerkon
    @Column(nullable = false)
    private Integer requestedSeats;

    // Data kur user-i futet ne waitlist
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Statusi aktual i waitlist
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private WaitlistStatus status = WaitlistStatus.WAITING;

    // Nje user mund te kete waitlist entries per evente te ndryshme
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Nje event mund te kete shume user ne waitlist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }    }
}