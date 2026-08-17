package com.lhind.event_booking_api.dto.eventseat;

import com.lhind.event_booking_api.dto.reference.SeatResponseShort;
import com.lhind.event_booking_api.entity.StatusSeat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class EventSeatResponse {

    private Long id;

    private SeatResponseShort seat;

    private StatusSeat statusSeat;

    private BigDecimal priceSeat;
}