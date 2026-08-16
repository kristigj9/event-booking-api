
package com.lhind.event_booking_api.dto.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VenueResponseShort {
    private Long id;
    private String venueName;
    private String venueCity;
}