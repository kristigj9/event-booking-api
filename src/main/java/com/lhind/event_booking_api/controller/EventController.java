package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.event.EventRequest;
import com.lhind.event_booking_api.dto.event.EventResponse;
import com.lhind.event_booking_api.entity.EventStatus;
import com.lhind.event_booking_api.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(
            EventService eventService
    ) {
        this.eventService = eventService;
    }

    // ORGANIZER / ADMIN
    // Krijon nje event te ri
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
    // Merr te gjithe eventet
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {

        List<EventResponse> events =
                eventService.getAllEvents();

        return ResponseEntity.ok(events);
    }

    // PUBLIC
    // Merr nje event sipas ID
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Long id
    ) {

        EventResponse response =
                eventService.getEventById(id);

        return ResponseEntity.ok(response);
    }

    // PUBLIC
    // Merr eventet sipas organizer-it
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
    // Merr eventet sipas statusit
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EventResponse>> getEventsByStatus(
            @PathVariable EventStatus status
    ) {

        List<EventResponse> events =
                eventService.getEventsByStatus(status);

        return ResponseEntity.ok(events);
    }

    // PUBLIC
    // Merr eventet sipas kategoris
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
    // Merr eventet sipas venue
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

    // ORGANIZER owner / ADMIN
    // Ben Update eventin
    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequest request
    ) {

        EventResponse response =
                eventService.updateEvent(
                        eventId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    // ORGANIZER owner / ADMIN
    // Fshin eventin
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