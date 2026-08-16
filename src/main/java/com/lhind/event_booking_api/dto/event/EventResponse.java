package com.lhind.event_booking_api.dto.event;

import com.lhind.event_booking_api.dto.reference.CategoryResponseShort;
import com.lhind.event_booking_api.dto.reference.UserResponseShort;
import com.lhind.event_booking_api.dto.reference.VenueResponseShort;
import com.lhind.event_booking_api.entity.EventStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventResponse {

    private Long id;

    private String eventName;

    private String eventDescription;

    private LocalDateTime eventStartDateTime;

    private LocalDateTime eventEndDateTime;

    private Integer eventTotalSeats;

    private Integer eventAvailableSeats;

    private BigDecimal ticketPrice;

    private EventStatus eventStatus;

    private UserResponseShort organizer;

    private VenueResponseShort venue;

    private CategoryResponseShort category;
}