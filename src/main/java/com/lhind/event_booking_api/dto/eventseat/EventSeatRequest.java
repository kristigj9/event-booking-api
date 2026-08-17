package com.lhind.event_booking_api.dto.eventseat;

import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EventSeatRequest {

    @Valid
    @NotNull(message = "Seat is required")
    private SeatSelectionRequest seat;

    @NotNull(message = "Seat price is required")
    @Positive(message = "Seat price must be greater than 0")
    private BigDecimal priceSeat;
}
