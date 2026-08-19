package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.eventseat.EventSeatRequest;
import com.lhind.event_booking_api.dto.eventseat.EventSeatResponse;
import com.lhind.event_booking_api.entity.StatusSeat;
import com.lhind.event_booking_api.service.EventSeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/event-seats")
@Tag(
        name = "Event Seats",
        description = "Endpoints for assigning venue seats to events and managing event-specific seat availability and pricing"
)
public class EventSeatController {

    private final EventSeatService eventSeatService;

    public EventSeatController(
            EventSeatService eventSeatService
    ) {
        this.eventSeatService = eventSeatService;
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Assign seat to event",
            description = "Assigns a venue seat to an event with an event-specific price. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Seat assigned to event successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event seat data"
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
                    description = "Event or seat not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Seat is already assigned to this event"
            )
    })
    @PostMapping("/event/{eventId}")
    public ResponseEntity<EventSeatResponse> createEventSeat(
            @PathVariable Long eventId,
            @Valid @RequestBody EventSeatRequest request
    ) {

        EventSeatResponse response =
                eventSeatService.createEventSeat(
                        eventId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUBLIC
    @Operation(
            summary = "Get event seat by id",
            description = "Returns an event-specific seat by its id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event seat retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event seat not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventSeatResponse> getEventSeatById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                eventSeatService.getEventSeatById(id)
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get seats by event",
            description = "Returns all seats assigned to a specific event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event seats retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventSeatResponse>> getSeatsByEvent(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                eventSeatService.getSeatsByEvent(eventId)
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get event seats by status",
            description = "Returns event seats filtered by status such as AVAILABLE, RESERVED or SOLD"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event seats retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid seat status"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
    @GetMapping("/event/{eventId}/status/{status}")
    public ResponseEntity<List<EventSeatResponse>> getSeatsByEventAndStatus(
            @PathVariable Long eventId,
            @PathVariable StatusSeat status
    ) {

        return ResponseEntity.ok(
                eventSeatService.getSeatsByEventAndStatus(
                        eventId,
                        status
                )
        );
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Update event seat price",
            description = "Updates the price of an event-specific seat. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event seat price updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Seat price must be greater than zero"
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
                    description = "Event seat not found"
            )
    })
    @PatchMapping("/{eventSeatId}/price")
    public ResponseEntity<EventSeatResponse> updatePrice(
            @PathVariable Long eventSeatId,
            @RequestParam BigDecimal priceSeat
    ) {

        return ResponseEntity.ok(
                eventSeatService.updatePrice(
                        eventSeatId,
                        priceSeat
                )
        );
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Delete event seat",
            description = "Removes a seat from an event. Only AVAILABLE event seats can be deleted. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Event seat deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Only available event seats can be deleted"
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
                    description = "Event seat not found"
            )
    })
    @DeleteMapping("/{eventSeatId}")
    public ResponseEntity<Void> deleteEventSeat(
            @PathVariable Long eventSeatId
    ) {

        eventSeatService.deleteEventSeat(eventSeatId);

        return ResponseEntity
                .noContent()
                .build();
    }
}