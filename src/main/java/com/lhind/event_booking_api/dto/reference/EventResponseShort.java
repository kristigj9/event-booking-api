package com.lhind.event_booking_api.dto.reference;


import com.lhind.event_booking_api.entity.EventStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EventResponseShort {

    private Long id;

    private String eventName;

    private LocalDateTime eventStartDateTime;

    private LocalDateTime eventEndDateTime;
    private EventStatus eventStatus;
}