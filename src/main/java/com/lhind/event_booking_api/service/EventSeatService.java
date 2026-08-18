package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.eventseat.EventSeatRequest;
import com.lhind.event_booking_api.dto.eventseat.EventSeatResponse;
import com.lhind.event_booking_api.entity.StatusSeat;

import java.math.BigDecimal;
import java.util.List;

public interface EventSeatService {

    EventSeatResponse createEventSeat(
            Long eventId,
            EventSeatRequest request
    );

    EventSeatResponse getEventSeatById(Long id);

    List<EventSeatResponse> getSeatsByEvent(Long eventId);

    List<EventSeatResponse> getSeatsByEventAndStatus(
            Long eventId,
            StatusSeat statusSeat
    );

    EventSeatResponse updatePrice(
            Long eventSeatId,
            BigDecimal priceSeat
    );

    void deleteEventSeat(Long eventSeatId);
}