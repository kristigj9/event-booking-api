package com.lhind.event_booking_api.dto.booking;

import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingRequest {

    private Long eventId;

    private List<SeatSelectionRequest> seats;
}