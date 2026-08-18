package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.eventseat.EventSeatRequest;
import com.lhind.event_booking_api.dto.eventseat.EventSeatResponse;
import com.lhind.event_booking_api.entity.StatusSeat;
import com.lhind.event_booking_api.service.EventSeatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/event-seats")
public class EventSeatController {

    private final EventSeatService eventSeatService;

    public EventSeatController(
            EventSeatService eventSeatService
    ) {
        this.eventSeatService = eventSeatService;
    }

    // ORGANIZER owner / ADMIN
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
    @GetMapping("/{id}")
    public ResponseEntity<EventSeatResponse> getEventSeatById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                eventSeatService.getEventSeatById(id)
        );
    }

    // PUBLIC
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<EventSeatResponse>> getSeatsByEvent(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                eventSeatService.getSeatsByEvent(eventId)
        );
    }

    // PUBLIC
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

    // ORGANIZER owner / ADMIN
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

    // ORGANIZER owner / ADMIN
    @DeleteMapping("/{eventSeatId}")
    public ResponseEntity<Void> deleteEventSeat(
            @PathVariable Long eventSeatId
    ) {

        eventSeatService.deleteEventSeat(eventSeatId);

        return ResponseEntity.noContent().build();
    }
}