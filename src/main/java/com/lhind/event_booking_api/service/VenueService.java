package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.venue.VenueRequest;
import com.lhind.event_booking_api.dto.venue.VenueResponse;

import java.util.List;

public interface VenueService {

    VenueResponse createVenue(VenueRequest request);

    VenueResponse getVenueById(Long id);

    List<VenueResponse> getAllVenues();

    VenueResponse updateVenue(
            Long id,
            VenueRequest request
    );

    void deleteVenue(Long id);
}