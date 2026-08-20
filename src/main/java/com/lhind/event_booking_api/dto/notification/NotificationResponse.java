package com.lhind.event_booking_api.dto.notification;

import com.lhind.event_booking_api.dto.reference.BookingResponseShort;
import com.lhind.event_booking_api.dto.reference.EventResponseShort;
import com.lhind.event_booking_api.dto.reference.UserResponseShort;
import com.lhind.event_booking_api.entity.NotificationStatus;
import com.lhind.event_booking_api.entity.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private NotificationType notificationType;

    private NotificationStatus notificationStatus;

    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    private UserResponseShort user;

    private EventResponseShort event;

    private BookingResponseShort booking;
}