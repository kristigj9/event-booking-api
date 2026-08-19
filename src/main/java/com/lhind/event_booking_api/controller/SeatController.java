package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.seat.SeatRequest;
import com.lhind.event_booking_api.dto.seat.SeatResponse;
import com.lhind.event_booking_api.service.SeatService;
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
@RequestMapping("/api/seats")
@Tag(
        name = "Seats",
        description = "Endpoints for retrieving and managing venue seats"
)
public class SeatController {

    private final SeatService seatService;

    public SeatController(
            SeatService seatService
    ) {
        this.seatService = seatService;
    }

    // ADMIN
    @Operation(
            summary = "Create seat",
            description = "Creates a new seat inside a venue. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Seat created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid seat data"
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Seat already exists in the venue"
            )
    })
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
    @Operation(
            summary = "Get all seats",
            description = "Returns all seats"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Seats retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<SeatResponse>> getAllSeats() {

        return ResponseEntity.ok(
                seatService.getAllSeats()
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get seat by id",
            description = "Returns a seat by its id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Seat retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Seat not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<SeatResponse> getSeatById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                seatService.getSeatById(id)
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get seats by venue",
            description = "Returns all seats that belong to a specific venue"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Seats retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Venue not found"
            )
    })
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<SeatResponse>> getSeatsByVenue(
            @PathVariable Long venueId
    ) {

        return ResponseEntity.ok(
                seatService.getSeatsByVenue(venueId)
        );
    }

    // ADMIN
    @Operation(
            summary = "Update seat",
            description = "Updates an existing seat. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Seat updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid seat data"
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
                    description = "Seat or venue not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Seat already exists in the venue"
            )
    })
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
    @Operation(
            summary = "Delete seat",
            description = "Deletes a seat by id. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Seat deleted successfully"
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
                    description = "Seat not found"
            )
    })
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