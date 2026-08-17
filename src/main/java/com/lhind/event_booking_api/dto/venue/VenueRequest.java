package com.lhind.event_booking_api.dto.venue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueRequest {

    @NotBlank(message = "Venue name is required")
    @Size(min = 2, max = 100,
            message = "Venue name must be between 2 and 100 characters")
    private String venueName;
    @NotBlank(message = "Venue address is required")
    @Size(min = 2, max = 200,
            message = "Venue address must be between 2 and 200 characters")
    private String venueAddress;
    @NotBlank(message = "Venue city is required")
    @Size(min = 2, max = 100,
            message = "Venue city must be between 2 and 100 characters")
    private String venueCity;

    @NotNull(message = "Venue capacity is required")
    @Positive(message = "Venue capacity must be greater than 0")
    private Integer venueCapacity;
}