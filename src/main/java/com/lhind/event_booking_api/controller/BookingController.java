package com.lhind.event_booking_api.controller;
import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.entity.BookingStatus;
import com.lhind.event_booking_api.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService
    ) {
        this.bookingService = bookingService;
    }

    // USER / ORGANIZER / ADMIN
    // Krijon booking per user-in e autentikuar
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request
    ) {

        BookingResponse response =
                bookingService.createBooking(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // USER owner / ADMIN
    // Merr nje booking sipas ID
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.getBookingById(bookingId)
        );
    }

    // USER / ORGANIZER / ADMIN
    // Merr booking-et personale
    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {

        return ResponseEntity.ok(
                bookingService.getMyBookings()
        );
    }

    // USER / ORGANIZER / ADMIN
    // Merr booking-et personale sipas statusit
    @GetMapping("/me/status/{status}")
    public ResponseEntity<List<BookingResponse>> getMyBookingsByStatus(
            @PathVariable BookingStatus status
    ) {

        return ResponseEntity.ok(
                bookingService.getMyBookingsByStatus(status)
        );
    }

    // ORGANIZER / ADMIN
    // Merr booking-et e nje eventi
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByEvent(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                bookingService.getBookingsByEvent(eventId)
        );
    }

    // ORGANIZER / ADMIN
    // Konfirmon booking
    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.confirmBooking(bookingId)
        );
    }

    // USER owner / ADMIN
    // Anulon booking
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.cancelBooking(bookingId)
        );
    }

    // ORGANIZER / ADMIN
    // Shenon booking si completed
    @PatchMapping("/{bookingId}/complete")
    public ResponseEntity<BookingResponse> completeBooking(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                bookingService.completeBooking(bookingId)
        );
    }
}