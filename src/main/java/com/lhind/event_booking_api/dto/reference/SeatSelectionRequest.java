package com.lhind.event_booking_api.dto.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatSelectionRequest {
    @NotBlank(message = "Row number is required")
    @Size(max = 20,
            message = "Row number must not exceed 20 characters")
    private String rowNumber;
    @NotNull(message = "Seat number is required")
    @Positive(message = "Seat number must be greater than 0")
    private Integer seatNumber;
}