package com.lhind.event_booking_api.dto.booking;

import com.lhind.event_booking_api.entity.BookingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingResponseShort {

    private Long id;
    private BookingStatus status;
}