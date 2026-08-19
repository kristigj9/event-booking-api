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

        List<EventResponse> events =
                eventService.getAllEvents();

        return ResponseEntity.ok(events);
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

        EventResponse response =
                eventService.getEventById(id);

        return ResponseEntity.ok(response);
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

        List<EventResponse> events =
                eventService.getEventsByOrganizer(
                        organizerId
                );

        return ResponseEntity.ok(events);
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

        List<EventResponse> events =
                eventService.getEventsByStatus(status);

        return ResponseEntity.ok(events);
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

        List<EventResponse> events =
                eventService.getEventsByCategory(
                        categoryId
                );

        return ResponseEntity.ok(events);
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

        List<EventResponse> events =
                eventService.getEventsByVenue(
                        venueId
                );

        return ResponseEntity.ok(events);
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

        EventResponse response =
                eventService.updateEvent(
                        eventId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Delete event",
            description = "Deletes an event. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Event deleted successfully"
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

        eventService.deleteEvent(eventId);

        return ResponseEntity
                .noContent()
                .build();
    }
}