
package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.entity.BookingStatus;

import java.util.List;

public interface BookingService {

    //BookingRepository
    //EventRepository
    //EventSeatRepository
    //SeatRepository
    //UserRepository
    //BookingSeat

        BookingResponse createBooking(
                Long userId,
                BookingRequest request
        );

        BookingResponse getBookingById(Long bookingId);

        List<BookingResponse> getBookingsByUser(Long userId);

        List<BookingResponse> getBookingsByEvent(Long eventId);

        List<BookingResponse> getBookingsByUserAndStatus(
                Long userId,
                BookingStatus status
        );

        BookingResponse confirmBooking(Long bookingId);

        BookingResponse cancelBooking(
                Long bookingId,
                Long userId
        );

        BookingResponse completeBooking(Long bookingId);
    }

