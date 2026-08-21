package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.entity.BookingStatus;
import com.lhind.event_booking_api.service.BookingService;
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
@RequestMapping("/api/bookings")
@Tag(
        name = "Bookings",
        description = "Endpoints for creating and managing event bookings"
)
public class BookingController {

    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService
    ) {
        this.bookingService = bookingService;
    }

    // USER / ORGANIZER / ADMIN
    @Operation(
            summary = "Create booking",
            description = "Creates a booking for the currently authenticated user. The event must be PUBLISHED and must not have started"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Booking created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid booking data, invalid event state, duplicate seats or unavailable seats"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event or seat not found"
            )
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request
    ) {

        BookingResponse response =
                bookingService.createBooking(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // USER OWNER / ADMIN
    @Operation(
            summary = "Get booking by id",
            description = "Returns a booking by id. Accessible by the booking owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking retrieved successfully"
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
                    description = "Booking not found"
            )
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.getBookingById(
                        bookingId
                )
        );
    }

    // USER / ORGANIZER / ADMIN
    @Operation(
            summary = "Get my bookings",
            description = "Returns all bookings of the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bookings retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {

        return ResponseEntity.ok(
                bookingService.getMyBookings()
        );
    }

    // USER / ORGANIZER / ADMIN
    @Operation(
            summary = "Get my bookings by status",
            description = "Returns bookings of the currently authenticated user filtered by booking status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bookings retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid booking status"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me/status/{status}")
    public ResponseEntity<List<BookingResponse>> getMyBookingsByStatus(
            @PathVariable BookingStatus status
    ) {

        return ResponseEntity.ok(
                bookingService.getMyBookingsByStatus(
                        status
                )
        );
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Get bookings by event",
            description = "Returns all bookings for an event. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bookings retrieved successfully"
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
    public ResponseEntity<List<BookingResponse>> getBookingsByEvent(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                bookingService.getBookingsByEvent(
                        eventId
                )
        );
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Confirm booking",
            description = "Confirms a PENDING booking and changes its reserved event seats to SOLD. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking confirmed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking cannot be confirmed in its current state"
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
                    description = "Booking not found"
            )
    })
    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.confirmBooking(
                        bookingId
                )
        );
    }

    // USER OWNER / ADMIN
    @Operation(
            summary = "Cancel booking",
            description = "Cancels a PENDING or CONFIRMED booking and releases its seats. COMPLETED bookings cannot be cancelled. Accessible by the booking owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking cannot be cancelled in its current state"
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
                    description = "Booking not found"
            )
    })
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(
                        bookingId
                )
        );
    }

    // ORGANIZER OWNER / ADMIN
    @Operation(
            summary = "Complete booking",
            description = "Marks a CONFIRMED booking as COMPLETED. Accessible by the event organizer or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Booking cannot be completed in its current state"
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
                    description = "Booking not found"
            )
    })
    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResponse> completeBooking(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.completeBooking(
                        bookingId
                )
        );
    }
}