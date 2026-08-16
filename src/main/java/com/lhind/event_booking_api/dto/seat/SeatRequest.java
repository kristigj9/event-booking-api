package com.lhind.event_booking_api.dto.seat;

import com.lhind.event_booking_api.dto.reference.VenueReferenceRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatRequest {

    private String rowNumber;

    private Integer seatNumber;

    private VenueReferenceRequest venue;
}