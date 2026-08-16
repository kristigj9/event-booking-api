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
                        columnNames = {"user_id", "event_id"}
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

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateTimeReview;


    //Lidhja e shume review nje user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
//Lidhja shume review me nje event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
//Gjenerimi Automatik i dates ores per nje Review
    @PrePersist
    public void prePersist() {
        this.dateTimeReview = LocalDateTime.now();
    }
}