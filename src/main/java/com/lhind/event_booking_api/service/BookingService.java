package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.entity.BookingStatus;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(
            BookingRequest request
    );

    BookingResponse getBookingById(Long bookingId);

    List<BookingResponse> getMyBookings();

    List<BookingResponse> getBookingsByEvent(Long eventId);

    List<BookingResponse> getMyBookingsByStatus(
            BookingStatus status
    );

    BookingResponse confirmBooking(Long bookingId);

    BookingResponse cancelBooking(Long bookingId);

    BookingResponse completeBooking(Long bookingId);
}