package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.dto.reference.BookingResponseShort;
import com.lhind.event_booking_api.dto.reference.EventResponseShort;
import com.lhind.event_booking_api.dto.reference.UserResponseShort;
import com.lhind.event_booking_api.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(
            Notification notification
    ) {

        if (notification == null) {
            return null;
        }

        UserResponseShort userResponse = null;

        if (notification.getUser() != null) {
            userResponse = UserResponseShort.builder()
                    .id(notification.getUser().getId())
                    .firstName(notification.getUser().getFirstName())
                    .lastName(notification.getUser().getLastName())
                    .email(notification.getUser().getEmail())
                    .build();
        }

        EventResponseShort eventResponse = null;

        if (notification.getEvent() != null) {
            eventResponse = EventResponseShort.builder()
                    .id(notification.getEvent().getId())
                    .eventName(notification.getEvent().getEventName())
                    .eventStartDateTime(
                            notification.getEvent()
                                    .getEventStartDateTime()
                    )
                    .build();
        }

        BookingResponseShort bookingResponse = null;

        if (notification.getBooking() != null) {
            bookingResponse = BookingResponseShort.builder()
                    .id(notification.getBooking().getId())
                    .status(
                            notification.getBooking()
                                    .getBookingStatus()
                    )
                    .build();
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
                .user(userResponse)
                .event(eventResponse)
                .booking(bookingResponse)
                .build();
    }

    public List<NotificationResponse> toResponseList(
            List<Notification> notifications
    ) {

        return notifications.stream()
                .map(this::toResponse)
                .toList();
    }
}