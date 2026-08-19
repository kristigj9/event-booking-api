package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.venue.VenueRequest;
import com.lhind.event_booking_api.dto.venue.VenueResponse;
import com.lhind.event_booking_api.service.VenueService;
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
@RequestMapping("/api/venues")
@Tag(
        name = "Venues",
        description = "Endpoints for retrieving and managing event venues"
)
public class VenueController {

    private final VenueService venueService;

    public VenueController(
            VenueService venueService
    ) {
        this.venueService = venueService;
    }

    // ADMIN
    @Operation(
            summary = "Create venue",
            description = "Creates a new venue. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Venue created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid venue data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
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
    @Operation(
            summary = "Get all venues",
            description = "Returns all available venues"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Venues retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAllVenues() {

        return ResponseEntity.ok(
                venueService.getAllVenues()
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get venue by id",
            description = "Returns a venue by its id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Venue retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Venue not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> getVenueById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                venueService.getVenueById(id)
        );
    }

    // ADMIN
    @Operation(
            summary = "Update venue",
            description = "Updates an existing venue. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Venue updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid venue data"
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
                    description = "Venue not found"
            )
    })
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
    @Operation(
            summary = "Delete venue",
            description = "Deletes a venue by id. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Venue deleted successfully"
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
                    description = "Venue not found"
            )
    })
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