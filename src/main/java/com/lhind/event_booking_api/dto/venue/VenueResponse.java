package com.lhind.event_booking_api.dto.venue;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VenueResponse {

    private Long id;

    private String venueName;

    private String venueAddress;

    private String venueCity;

    private Integer venueCapacity;
}