package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.event.EventRequest;
import com.lhind.event_booking_api.dto.event.EventResponse;
import com.lhind.event_booking_api.dto.event.EventUpdateRequest;
import com.lhind.event_booking_api.entity.EventStatus;
import com.lhind.event_booking_api.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(
        name = "Events",
        description = "Endpoints for creating, retrieving, updating and deleting events"
)
public class EventController {

    private final EventService eventService;

    public EventController(
            EventService eventService
    ) {
        this.eventService = eventService;
    }

    // ORGANIZER / ADMIN
    @Operation(
            summary = "Create event",
            description = "Creates a new event. Accessible by ORGANIZER and ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Event created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event data"
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
                    description = "Venue or category not found"
            )
    })
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody EventRequest request
    ) {

        EventResponse response =
                eventService.createEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUBLIC
    @Operation(
            summary = "Get all events",
            description = "Returns all events"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Events retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {

        return ResponseEntity.ok(
                eventService.getAllEvents()
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get event by id",
            description = "Returns a single event by its id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                eventService.getEventById(
                        id
                )
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get events by organizer",
            description = "Returns all events created by a specific organizer"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Events retrieved successfully"
    )
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventResponse>> getEventsByOrganizer(
            @PathVariable Long organizerId
    ) {

        return ResponseEntity.ok(
                eventService.getEventsByOrganizer(
                        organizerId
                )
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get events by status",
            description = "Returns all events with the specified status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Events retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event status"
            )
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventResponse>> getEventsByStatus(
            @PathVariable EventStatus status
    ) {

        return ResponseEntity.ok(
                eventService.getEventsByStatus(
                        status
                )
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get events by category",
            description = "Returns all events assigned to a specific category"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Events retrieved successfully"
    )
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<EventResponse>> getEventsByCategory(
            @PathVariable Long categoryId
    ) {

        return ResponseEntity.ok(
                eventService.getEventsByCategory(
                        categoryId
                )
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get events by venue",
            description = "Returns all events assigned to a specific venue"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Events retrieved successfully"
    )
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<EventResponse>> getEventsByVenue(
            @PathVariable Long venueId
    ) {

        return ResponseEntity.ok(
                eventService.getEventsByVenue(
                        venueId
                )
        );
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Update event",
            description = "Updates an event. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event data or invalid status transition"
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
                    description = "Event, venue or category not found"
            )
    })
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventUpdateRequest request
    ) {

        return ResponseEntity.ok(
                eventService.updateEvent(
                        eventId,
                        request
                )
        );
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Delete event",
            description = "Deletes a draft or cancelled event. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Event deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Event cannot be deleted because of its status or existing bookings/waitlists"
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
    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long eventId
    ) {

        eventService.deleteEvent(
                eventId
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}