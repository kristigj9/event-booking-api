package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.WaitlistStatus;
import com.lhind.event_booking_api.service.WaitlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "Join event waitlist",
            description = "Adds the authenticated user to the waitlist of a PUBLISHED event when enough seats are not currently available"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User joined the waitlist successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Waitlist cannot be joined because of event state, duplicate waitlist or available seats"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
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
    @Operation(
            summary = "Get my waitlists",
            description = "Returns all waitlist entries of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Waitlist entries retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/my")
    public ResponseEntity<List<WaitlistResponse>> getMyWaitlists() {

        return ResponseEntity.ok(
                waitlistService.getMyWaitlists()
        );
    }

    // USER OWNER / ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Get waitlist by ID",
            description = "Returns a waitlist entry by ID. Accessible by the waitlist owner, event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Waitlist retrieved successfully"
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
                    description = "Waitlist not found"
            )
    })
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

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Get event waitlist",
            description = "Returns the waitlist of an event ordered by creation date. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event waitlist retrieved successfully"
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
                    description = "Event not found"
            )
    })
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

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Get event waitlist by status",
            description = "Returns waitlist entries of an event filtered by status. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Filtered event waitlist retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid waitlist status"
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
                    description = "Event not found"
            )
    })
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

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Mark waitlist as notified",
            description = "Changes a WAITING waitlist entry to NOTIFIED and creates a WAITLIST_AVAILABLE notification"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Waitlist marked as notified successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Only a WAITING waitlist can be marked as NOTIFIED"
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
                    description = "Waitlist not found"
            )
    })
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

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Mark waitlist as converted",
            description = "Changes a NOTIFIED waitlist entry to CONVERTED"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Waitlist converted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Only a NOTIFIED waitlist can be converted"
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
                    description = "Waitlist not found"
            )
    })
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

    // USER OWNER
    @Operation(
            summary = "Cancel waitlist",
            description = "Cancels a WAITING or NOTIFIED waitlist entry owned by the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Waitlist cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Waitlist is already cancelled or has already been converted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not own this waitlist"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Waitlist not found"
            )
    })
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