package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(
        name = "Notifications",
        description = "Notification management endpoints"
)
public class NotificationController {

    private final NotificationService notificationService;


    // USER / ORGANIZER / ADMIN
    // Merr te gjitha notifications e user-it aktual
    @Operation(
            summary = "Get my notifications",
            description = "Returns all notifications of the authenticated user"
    )
    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {

        return ResponseEntity.ok(
                notificationService.getMyNotifications()
        );
    }


    // USER / ORGANIZER / ADMIN
    // Merr vetem notifications UNREAD
    @Operation(
            summary = "Get my unread notifications",
            description = "Returns all unread notifications of the authenticated user"
    )
    @GetMapping("/me/unread")
    public ResponseEntity<List<NotificationResponse>> getMyUnreadNotifications() {

        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications()
        );
    }


    // USER / ORGANIZER / ADMIN
    // Numeron notifications UNREAD
    @Operation(
            summary = "Count my unread notifications",
            description = "Returns the number of unread notifications of the authenticated user"
    )
    @GetMapping("/me/unread/count")
    public ResponseEntity<Long> countMyUnreadNotifications() {

        return ResponseEntity.ok(
                notificationService.countMyUnreadNotifications()
        );
    }


    // USER owner / ADMIN
    // Ownership kontrollohet ne Service
    @Operation(
            summary = "Get notification by ID",
            description = "Returns a notification by ID. Access is allowed to the owner or admin"
    )
    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @PathVariable Long notificationId
    ) {

        return ResponseEntity.ok(
                notificationService.getNotificationById(
                        notificationId
                )
        );
    }


    // USER owner / ADMIN
    // UNREAD -> READ
    @Operation(
            summary = "Mark notification as read",
            description = "Changes the notification status from UNREAD to READ"
    )
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId
    ) {

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        notificationId
                )
        );
    }


    // USER / ORGANIZER / ADMIN
    // Te gjitha UNREAD -> READ
    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications of the authenticated user as read"
    )
    @PatchMapping("/me/read-all")
    public ResponseEntity<Void> markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity.noContent().build();
    }
}