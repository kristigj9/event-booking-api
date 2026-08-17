package com.lhind.event_booking_api.mapper;
import com.lhind.event_booking_api.dto.eventseat.EventSeatResponse;
import com.lhind.event_booking_api.entity.EventSeat;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class EventSeatMapper {

    private final SeatMapper seatMapper;

    public EventSeatMapper(SeatMapper seatMapper) {
        this.seatMapper = seatMapper;
    }

    // EventSeat -> EventSeatResponse
    public EventSeatResponse toResponse(EventSeat eventSeat) {

        if (eventSeat == null) {
            return null;
        }

        return EventSeatResponse.builder()
                .id(eventSeat.getId())
                .seat(seatMapper.toShortResponse(eventSeat.getSeat()))
                .statusSeat(eventSeat.getStatusSeat())
                .priceSeat(eventSeat.getPriceSeat())
                .build();
    }

    // List<EventSeat> -> List<EventSeatResponse>
    public List<EventSeatResponse> toResponseList(List<EventSeat> eventSeats) {

        if (eventSeats == null) {
            return Collections.emptyList();
        }

        return eventSeats.stream()
                .map(this::toResponse)
                .toList();
    }
}