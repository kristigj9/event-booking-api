package com.lhind.event_booking_api.dto.event;

import com.lhind.event_booking_api.dto.reference.CategoryReferenceRequest;
import com.lhind.event_booking_api.dto.reference.VenueReferenceRequest;
import com.lhind.event_booking_api.entity.EventStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventRequest {

    private String eventName;

    private String eventDescription;

    private LocalDateTime eventStartDateTime;

    private LocalDateTime eventEndDateTime;

    private Integer eventTotalSeats;

    private BigDecimal ticketPrice;

    private EventStatus eventStatus;

    private VenueReferenceRequest venue;

    private CategoryReferenceRequest category;
}