package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.dto.reference.BookingResponseShort;
import com.lhind.event_booking_api.entity.Booking;
import com.lhind.event_booking_api.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class NotificationMapper {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public NotificationMapper(
            UserMapper userMapper,
            EventMapper eventMapper
    ) {
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }

    public NotificationResponse toResponse(
            Notification notification
    ) {

        if (notification == null) {
            return null;
        }

        return NotificationResponse.builder()
                .id(notification.getId())
                .notificationType(
                        notification.getNotificationType()
                )
                .notificationStatus(
                        notification.getNotificationStatus()
                )
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .user(
                        userMapper.toShortResponse(
                                notification.getUser()
                        )
                )
                .event(
                        eventMapper.toShortResponse(
                                notification.getEvent()
                        )
                )
                .booking(
                        toBookingResponseShort(
                                notification.getBooking()
                        )
                )
                .build();
    }

    public List<NotificationResponse> toResponseList(
            List<Notification> notifications
    ) {

        if (notifications == null) {
            return Collections.emptyList();
        }

        return notifications.stream()
                .map(this::toResponse)
                .toList();
    }

    private BookingResponseShort toBookingResponseShort(
            Booking booking
    ) {

        if (booking == null) {
            return null;
        }

        return BookingResponseShort.builder()
                .id(booking.getId())
                .status(
                        booking.getBookingStatus()
                )
                .build();
    }
}