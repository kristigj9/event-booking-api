package com.lhind.event_booking_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationStatus notificationStatus =
            NotificationStatus.UNREAD;

    @Column(nullable = false)
    private String message;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    // User qe merr notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    // Eventi me te cilin lidhet notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // Booking me te cilin lidhet notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @PrePersist
    public void prePersist() {

        if (this.notificationStatus == null) {
            this.notificationStatus =
                    NotificationStatus.UNREAD;
        }

        if (this.createdAt == null) {
            this.createdAt =
                    LocalDateTime.now();
        }
    }
}