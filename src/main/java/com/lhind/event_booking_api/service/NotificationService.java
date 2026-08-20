package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.entity.Booking;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.NotificationType;
import com.lhind.event_booking_api.entity.User;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(
            User user,
            Event event,
            Booking booking,
            NotificationType notificationType,
            String message
    );

    NotificationResponse getNotificationById(
            Long notificationId
    );

    List<NotificationResponse> getMyNotifications();

    List<NotificationResponse> getMyUnreadNotifications();

    long countMyUnreadNotifications();

    NotificationResponse markAsRead(
            Long notificationId
    );

    void markAllAsRead();
}