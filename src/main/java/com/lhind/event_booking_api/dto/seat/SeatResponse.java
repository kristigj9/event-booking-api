package com.lhind.event_booking_api.dto.seat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeatResponse {

    private Long id;

    private String rowNumber;

    private Integer seatNumber;

    private Long venueId;
}