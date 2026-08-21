package com.lhind.event_booking_api.dto.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueReferenceRequest {

    @NotBlank(message = "Venue name is required")
    @Size(
            min = 2,
            max = 100,
            message = "Venue name must be between 2 and 100 characters"
    )
    private String venueName;

    @NotBlank(message = "Venue city is required")
    @Size(
            min = 2,
            max = 100,
            message = "Venue city must be between 2 and 100 characters"
    )
    private String venueCity;
}