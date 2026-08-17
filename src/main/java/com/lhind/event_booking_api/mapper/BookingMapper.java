package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.dto.reference.SeatResponseShort;
import com.lhind.event_booking_api.entity.Booking;
import com.lhind.event_booking_api.entity.BookingSeat;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BookingMapper {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;
    private final SeatMapper seatMapper;

    public BookingMapper(
            UserMapper userMapper,
            EventMapper eventMapper,
            SeatMapper seatMapper
    ) {
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
        this.seatMapper = seatMapper;
    }

    // BookingRequest + User + Event -> Booking
    public Booking toEntity(
            BookingRequest request,
            User user,
            Event event
    ) {


        if (request == null) {
            return null;
        }

        return Booking.builder()
                .seatsBooked(
                        request.getSeats() != null
                                ? request.getSeats().size()
                                : 0
                )
                .user(user)
                .event(event)
                .build();
    }

    // Booking -> BookingResponse
    public BookingResponse toResponse(Booking booking) {

        if (booking == null) {
            return null;
        }

        return BookingResponse.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .seatsBooked(booking.getSeatsBooked())
                .bookingStatus(booking.getBookingStatus())
                .user(userMapper.toShortResponse(booking.getUser()))
                .event(eventMapper.toShortResponse(booking.getEvent()))
                .seats(toSeatResponseList(booking.getBookingSeats()))
                .build();
    }

    // List<Booking> -> List<BookingResponse>
    public List<BookingResponse> toResponseList(List<Booking> bookings) {

        if (bookings == null) {
            return Collections.emptyList();
        }

        return bookings.stream()
                .map(this::toResponse)
                .toList();
    }

    // List<BookingSeat> -> List<SeatResponseShort>
    private List<SeatResponseShort> toSeatResponseList(
            List<BookingSeat> bookingSeats
    ) {

        if (bookingSeats == null) {
            return Collections.emptyList();
        }

        return bookingSeats.stream()
                .map(BookingSeat::getEventSeat)
                .filter(eventSeat -> eventSeat != null)
                .map(eventSeat -> eventSeat.getSeat())
                .filter(seat -> seat != null)
                .map(seatMapper::toShortResponse)
                .toList();
    }
}