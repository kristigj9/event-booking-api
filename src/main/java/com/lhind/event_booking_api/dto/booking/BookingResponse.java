package com.lhind.event_booking_api.dto.booking;

import com.lhind.event_booking_api.dto.reference.EventResponseShort;
import com.lhind.event_booking_api.dto.reference.SeatResponseShort;
import com.lhind.event_booking_api.dto.reference.UserResponseShort;
import com.lhind.event_booking_api.entity.BookingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class BookingResponse {

    private Long id;

    private LocalDateTime bookingDate;

    private Integer seatsBooked;

    private BookingStatus bookingStatus;

    private UserResponseShort user;

    private EventResponseShort event;

    private List<SeatResponseShort> seats;
}