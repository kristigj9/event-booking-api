package com.lhind.event_booking_api.dto.waitlist;

import com.lhind.event_booking_api.dto.reference.EventResponseShort;
import com.lhind.event_booking_api.dto.reference.UserResponseShort;
import com.lhind.event_booking_api.entity.WaitlistStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class WaitlistResponse {

    private Long id;
    private Integer requestedSeats;
    private LocalDateTime createdAt;
    private WaitlistStatus status;

    private UserResponseShort user;
    private EventResponseShort event;
}