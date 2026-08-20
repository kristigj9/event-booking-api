package com.lhind.event_booking_api.dto.waitlist;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaitlistRequest {

    @NotNull
    @Min(value = 1, message = "Requested seats must be at least 1")
    private Integer requestedSeats;
}