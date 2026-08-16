package com.lhind.event_booking_api.dto.venue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueRequest {

    private String venueName;

    private String venueAddress;

    private String venueCity;

    private Integer venueCapacity;
}