package com.lhind.event_booking_api.dto.reference;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueReferenceRequest {
    @NotBlank(message = "Venue name is required")
    private String venueName;

    @NotBlank(message = "Venue city is required")
    private String venueCity;
}