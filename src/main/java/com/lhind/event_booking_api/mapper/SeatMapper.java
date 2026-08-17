package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.reference.SeatResponseShort;
import com.lhind.event_booking_api.dto.seat.SeatRequest;
import com.lhind.event_booking_api.dto.seat.SeatResponse;
import com.lhind.event_booking_api.entity.Seat;
import com.lhind.event_booking_api.entity.Venue;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class SeatMapper {

    // SeatRequest + Venue -> Seat
    public Seat toEntity(
            SeatRequest request,
            Venue venue
    ) {

        if (request == null) {
            return null;
        }

        return Seat.builder()
                .rowNumber(request.getRowNumber())
                .seatNumber(request.getSeatNumber())
                .venue(venue)
                .build();
    }

    // Seat -> SeatResponse
    public SeatResponse toResponse(Seat seat) {

        if (seat == null) {
            return null;
        }

        return SeatResponse.builder()
                .id(seat.getId())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .venueId(
                        seat.getVenue() != null
                                ? seat.getVenue().getId()
                                : null
                )
                .build();
    }

    // Seat -> SeatResponseShort
    public SeatResponseShort toShortResponse(Seat seat) {

        if (seat == null) {
            return null;
        }

        return SeatResponseShort.builder()
                .id(seat.getId())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .build();
    }

    // List<Seat> -> List<SeatResponse>
    public List<SeatResponse> toResponseList(List<Seat> seats) {

        if (seats == null) {
            return Collections.emptyList();
        }

        return seats.stream()
                .map(this::toResponse)
                .toList();
    }

    // List<Seat> -> List<SeatResponseShort>
    public List<SeatResponseShort> toShortResponseList(List<Seat> seats) {

        if (seats == null) {
            return Collections.emptyList();
        }

        return seats.stream()
                .map(this::toShortResponse)
                .toList();
    }

    // SeatRequest + Seat ekzistues + Venue -> update
    public void updateEntity(
            SeatRequest request,
            Seat seat,
            Venue venue
    ) {

        if (request == null || seat == null) {
            return;
        }

        seat.setRowNumber(request.getRowNumber());
        seat.setSeatNumber(request.getSeatNumber());
        seat.setVenue(venue);
    }
}