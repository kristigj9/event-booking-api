package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.reference.VenueReferenceRequest;
import com.lhind.event_booking_api.dto.reference.VenueResponseShort;
import com.lhind.event_booking_api.dto.venue.VenueRequest;
import com.lhind.event_booking_api.dto.venue.VenueResponse;
import com.lhind.event_booking_api.entity.Venue;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class VenueMapper {

    // VenueRequest -> Venue
    public Venue toEntity(VenueRequest request) {

        if (request == null) {
            return null;
        }

        return Venue.builder()
                .venueName(request.getVenueName())
                .venueAddress(request.getVenueAddress())
                .venueCity(request.getVenueCity())
                .venueCapacity(request.getVenueCapacity())
                .build();
    }

    // VenueReferenceRequest -> Venue

    public Venue toEntityReference(VenueReferenceRequest request) {

        if (request == null) {
            return null;
        }

        return Venue.builder()
                .venueName(request.getVenueName())
                .venueCity(request.getVenueCity())
                .build();
    }

    //  Venue ->  VenueResponse

    public VenueResponse toResponse(Venue venue) {

        if (venue == null) {
            return null;
        }

        return VenueResponse.builder()
                .id(venue.getId())
                .venueName(venue.getVenueName())
                .venueAddress(venue.getVenueAddress())
                .venueCity(venue.getVenueCity())
                .venueCapacity(venue.getVenueCapacity())
                .build();
    }

    // Venue ->VenueResponseShort

    public VenueResponseShort toShortResponse(Venue venue) {

        if (venue == null) {
            return null;
        }

        return VenueResponseShort.builder()
                .id(venue.getId())
                .venueName(venue.getVenueName())
                .venueCity(venue.getVenueCity())
                .build();
    }

    // List<Venue> -> List<VenueResponse>
    public List<VenueResponse> toResponseList(List<Venue> venues) {

        if (venues == null) {
            return Collections.emptyList();
        }

        return venues.stream()
                .map(this::toResponse)
                .toList();
    }
    //VenueRequest,Venue
    public void updateEntity(VenueRequest request, Venue venue) {

        if (request == null || venue == null) {
            return;
        }

        venue.setVenueName(request.getVenueName());
        venue.setVenueAddress(request.getVenueAddress());
        venue.setVenueCity(request.getVenueCity());
        venue.setVenueCapacity(request.getVenueCapacity());
    }
}