package com.lhind.event_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_user_event",
                        columnNames = {
                                "user_id",
                                "event_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer ratingReview;

    @Column(length = 1000)
    private String commentReview;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime dateTimeReview;

    // Nje user mund te kete disa reviews
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    // Nje event mund te kete disa reviews
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "event_id",
            nullable = false
    )
    private Event event;

    // Gjeneron automatikisht daten dhe oren e review
    @PrePersist
    public void prePersist() {

        if (this.dateTimeReview == null) {
            this.dateTimeReview = LocalDateTime.now();
        }
    }
}