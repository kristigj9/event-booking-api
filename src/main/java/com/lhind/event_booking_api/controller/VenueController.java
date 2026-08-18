package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.venue.VenueRequest;
import com.lhind.event_booking_api.dto.venue.VenueResponse;
import com.lhind.event_booking_api.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(
            VenueService venueService
    ) {
        this.venueService = venueService;
    }

    // ADMIN
    // Krijon venue
    @PostMapping
    public ResponseEntity<VenueResponse> createVenue(
            @Valid @RequestBody VenueRequest request
    ) {

        VenueResponse response =
                venueService.createVenue(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUBLIC
    // Merr te gjitha venues
    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAllVenues() {

        return ResponseEntity.ok(
                venueService.getAllVenues()
        );
    }

    // PUBLIC
    // Merr venue sipas ID
    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> getVenueById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                venueService.getVenueById(id)
        );
    }

    // ADMIN
    // Update venue
    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> updateVenue(
            @PathVariable Long id,
            @Valid @RequestBody VenueRequest request
    ) {

        return ResponseEntity.ok(
                venueService.updateVenue(
                        id,
                        request
                )
        );
    }

    // ADMIN
    // Fshin venue
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(
            @PathVariable Long id
    ) {

        venueService.deleteVenue(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}