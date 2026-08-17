package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.venue.VenueRequest;
import com.lhind.event_booking_api.dto.venue.VenueResponse;
import com.lhind.event_booking_api.entity.Venue;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.VenueMapper;
import com.lhind.event_booking_api.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    public VenueServiceImpl(
            VenueRepository venueRepository,
            VenueMapper venueMapper
    ) {
        this.venueRepository = venueRepository;
        this.venueMapper = venueMapper;
    }

    @Override
    @Transactional
    public VenueResponse createVenue(VenueRequest request) {

        Venue venue = venueMapper.toEntity(request);

        Venue savedVenue = venueRepository.save(venue);

        return venueMapper.toResponse(savedVenue);
    }

    @Override
    @Transactional(readOnly = true)
    public VenueResponse getVenueById(Long id) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Venue not found with id: " + id
                        )
                );

        return venueMapper.toResponse(venue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> getAllVenues() {

        List<Venue> venues = venueRepository.findAll();

        return venueMapper.toResponseList(venues);
    }

    @Override
    @Transactional
    public VenueResponse updateVenue(
            Long id,
            VenueRequest request
    ) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Venue not found with id: " + id
                        )
                );

        venueMapper.updateEntity(request, venue);

        Venue updatedVenue = venueRepository.save(venue);

        return venueMapper.toResponse(updatedVenue);
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {

        Venue venue = venueRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Venue not found with id: " + id
                        )
                );

        venueRepository.delete(venue);
    }
}