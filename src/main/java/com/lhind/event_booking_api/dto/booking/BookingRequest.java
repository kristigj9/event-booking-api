package com.lhind.event_booking_api.dto.booking;

import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookingRequest {
    @NotNull(message = "Event id is required")
    @Positive(message = "Event id must be greater than 0")
    private Long eventId;
    @Valid
    @NotEmpty(message = "At least one seat must be selected")
    private List<SeatSelectionRequest> seats;
}