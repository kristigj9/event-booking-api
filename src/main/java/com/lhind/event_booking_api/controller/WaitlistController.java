package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.WaitlistStatus;
import com.lhind.event_booking_api.service.WaitlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waitlists")
@RequiredArgsConstructor
@Tag(
        name = "Waitlists",
        description = "Waitlist management endpoints"
)
public class WaitlistController {

    private final WaitlistService waitlistService;


    // USER / ORGANIZER / ADMIN
    // Shton user-in e autentikuar ne waitlist te nje eventi
    @Operation(
            summary = "Join event waitlist",
            description = "Adds the authenticated user to the waitlist of an event"
    )
    @PostMapping("/event/{eventId}")
    public ResponseEntity<WaitlistResponse> joinWaitlist(
            @PathVariable Long eventId,
            @Valid @RequestBody WaitlistRequest request
    ) {

        WaitlistResponse response =
                waitlistService.joinWaitlist(
                        eventId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // USER / ORGANIZER / ADMIN
    // Merr waitlist entries te user-it te autentikuar
    @Operation(
            summary = "Get my waitlists",
            description = "Returns all waitlist entries of the authenticated user"
    )
    @GetMapping("/my")
    public ResponseEntity<List<WaitlistResponse>> getMyWaitlists() {

        return ResponseEntity.ok(
                waitlistService.getMyWaitlists()
        );
    }


    // USER owner / ORGANIZER owner / ADMIN
    // Ownership kontrollohet  Service
    @Operation(
            summary = "Get waitlist by ID",
            description = "Returns a waitlist entry by ID. Access is allowed to the owner, event organizer or admin"
    )
    @GetMapping("/{waitlistId}")
    public ResponseEntity<WaitlistResponse> getWaitlistById(
            @PathVariable Long waitlistId
    ) {

        return ResponseEntity.ok(
                waitlistService.getWaitlistById(
                        waitlistId
                )
        );
    }


    // ORGANIZER owner / ADMIN
    // Ownership i eventit kontrollohet ne Service
    @Operation(
            summary = "Get event waitlist",
            description = "Returns the waitlist of an event ordered by creation date. Access is allowed to the event organizer or admin"
    )
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<WaitlistResponse>> getWaitlistByEvent(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                waitlistService.getWaitlistByEvent(
                        eventId
                )
        );
    }


    // ORGANIZER owner / ADMIN
    // Filtron waitlist-in e eventit sipas statusit
    @Operation(
            summary = "Get event waitlist by status",
            description = "Returns waitlist entries of an event filtered by status"
    )
    @GetMapping("/event/{eventId}/status/{status}")
    public ResponseEntity<List<WaitlistResponse>> getWaitlistByEventAndStatus(
            @PathVariable Long eventId,
            @PathVariable WaitlistStatus status
    ) {

        return ResponseEntity.ok(
                waitlistService.getWaitlistByEventAndStatus(
                        eventId,
                        status
                )
        );
    }


    // ORGANIZER owner / ADMIN
    // WAITING -> NOTIFIED
    @Operation(
            summary = "Mark waitlist as notified",
            description = "Changes the waitlist status from WAITING to NOTIFIED"
    )
    @PatchMapping("/{waitlistId}/notify")
    public ResponseEntity<WaitlistResponse> markAsNotified(
            @PathVariable Long waitlistId
    ) {

        return ResponseEntity.ok(
                waitlistService.markAsNotified(
                        waitlistId
                )
        );
    }


    // ORGANIZER owner / ADMIN
    // NOTIFIED -> CONVERTED
    @Operation(
            summary = "Mark waitlist as converted",
            description = "Changes the waitlist status from NOTIFIED to CONVERTED"
    )
    @PatchMapping("/{waitlistId}/convert")
    public ResponseEntity<WaitlistResponse> markAsConverted(
            @PathVariable Long waitlistId
    ) {

        return ResponseEntity.ok(
                waitlistService.markAsConverted(
                        waitlistId
                )
        );
    }


    // USER owner
    // WAITING / NOTIFIED -> CANCELLED
    @Operation(
            summary = "Cancel waitlist",
            description = "Cancels a waitlist entry owned by the authenticated user"
    )
    @PatchMapping("/{waitlistId}/cancel")
    public ResponseEntity<WaitlistResponse> cancelWaitlist(
            @PathVariable Long waitlistId
    ) {

        return ResponseEntity.ok(
                waitlistService.cancelWaitlist(
                        waitlistId
                )
        );
    }
}