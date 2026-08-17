package com.lhind.event_booking_api.service;
import com.lhind.event_booking_api.dto.seat.SeatRequest;
import com.lhind.event_booking_api.dto.seat.SeatResponse;

import java.util.List;

public interface SeatService {

    SeatResponse createSeat(SeatRequest request);

    SeatResponse getSeatById(Long id);

    List<SeatResponse> getAllSeats();

    List<SeatResponse> getSeatsByVenue(Long venueId);

    SeatResponse updateSeat(
            Long id,
            SeatRequest request
    );

    void deleteSeat(Long id);
}