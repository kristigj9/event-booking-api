package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "Get my notifications",
            description = "Returns all notifications of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notifications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {

        return ResponseEntity.ok(
                notificationService.getMyNotifications()
        );
    }

    // USER / ORGANIZER / ADMIN
    @Operation(
            summary = "Get my unread notifications",
            description = "Returns all unread notifications of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Unread notifications retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me/unread")
    public ResponseEntity<List<NotificationResponse>> getMyUnreadNotifications() {

        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications()
        );
    }

    // USER / ORGANIZER / ADMIN
    @Operation(
            summary = "Count my unread notifications",
            description = "Returns the number of unread notifications of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Unread notification count retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me/unread/count")
    public ResponseEntity<Long> countMyUnreadNotifications() {

        return ResponseEntity.ok(
                notificationService.countMyUnreadNotifications()
        );
    }

    // USER OWNER / ADMIN
    @Operation(
            summary = "Get notification by ID",
            description = "Returns a notification by ID. Accessible by the notification owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found"
            )
    })
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

    // USER OWNER / ADMIN
    @Operation(
            summary = "Mark notification as read",
            description = "Changes a notification from UNREAD to READ. Accessible by the notification owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification marked as read successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found"
            )
    })
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
    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications of the authenticated user as READ"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "All unread notifications marked as read successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @PatchMapping("/me/read-all")
    public ResponseEntity<Void> markAllAsRead() {

        notificationService.markAllAsRead();

        return ResponseEntity
                .noContent()
                .build();
    }
}