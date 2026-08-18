package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.seat.SeatRequest;
import com.lhind.event_booking_api.dto.seat.SeatResponse;
import com.lhind.event_booking_api.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(
            SeatService seatService
    ) {
        this.seatService = seatService;
    }

    // ADMIN
    // Krijon nje Seat te ri
    @PostMapping
    public ResponseEntity<SeatResponse> createSeat(
            @Valid @RequestBody SeatRequest request
    ) {

        SeatResponse response =
                seatService.createSeat(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUBLIC
    // Merr te gjith Seat-et
    @GetMapping
    public ResponseEntity<List<SeatResponse>> getAllSeats() {

        return ResponseEntity.ok(
                seatService.getAllSeats()
        );
    }

    // PUBLIC
    // Merr nje Seat sipas ID
    @GetMapping("/{id}")
    public ResponseEntity<SeatResponse> getSeatById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                seatService.getSeatById(id)
        );
    }

    // PUBLIC
    // Merr Seat-et e nje Venue
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<SeatResponse>> getSeatsByVenue(
            @PathVariable Long venueId
    ) {

        return ResponseEntity.ok(
                seatService.getSeatsByVenue(venueId)
        );
    }

    // ADMIN
    // Update Seat
    @PutMapping("/{id}")
    public ResponseEntity<SeatResponse> updateSeat(
            @PathVariable Long id,
            @Valid @RequestBody SeatRequest request
    ) {

        return ResponseEntity.ok(
                seatService.updateSeat(
                        id,
                        request
                )
        );
    }

    // ADMIN
    // Fshin Seat
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeat(
            @PathVariable Long id
    ) {

        seatService.deleteSeat(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}