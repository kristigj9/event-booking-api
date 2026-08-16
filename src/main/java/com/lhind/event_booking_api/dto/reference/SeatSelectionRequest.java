package com.lhind.event_booking_api.dto.reference;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatSelectionRequest {

    private String rowNumber;
    private Integer seatNumber;
}