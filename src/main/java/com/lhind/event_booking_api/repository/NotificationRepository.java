package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Notification;
import com.lhind.event_booking_api.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<Notification> findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
            Long userId,
            NotificationStatus notificationStatus
    );

    long countByUserIdAndNotificationStatus(
            Long userId,
            NotificationStatus notificationStatus
    );

    List<Notification> findByEventIdOrderByCreatedAtDesc(
            Long eventId
    );

    List<Notification> findByBookingIdOrderByCreatedAtDesc(
            Long bookingId
    );
}