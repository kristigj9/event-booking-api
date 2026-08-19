package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.venue.VenueRequest;
import com.lhind.event_booking_api.dto.venue.VenueResponse;
import com.lhind.event_booking_api.entity.Venue;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.VenueMapper;
import com.lhind.event_booking_api.repository.VenueRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    private static final Logger log =
            LogManager.getLogger(VenueServiceImpl.class);

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    public VenueServiceImpl(
            VenueRepository venueRepository,
            VenueMapper venueMapper
    ) {
        this.venueRepository = venueRepository;
        this.venueMapper = venueMapper;
    }

    // CREATE
    @Override
    @Transactional
    public VenueResponse createVenue(
            VenueRequest request
    ) {

        log.info(
                "Creating venue with name: {} in city: {}",
                request.getVenueName(),
                request.getVenueCity()
        );

        Venue venue =
                venueMapper.toEntity(request);

        Venue savedVenue =
                venueRepository.save(venue);

        log.info(
                "Venue created successfully with id: {}",
                savedVenue.getId()
        );

        return venueMapper.toResponse(savedVenue);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public VenueResponse getVenueById(
            Long id
    ) {

        log.debug(
                "Fetching venue by id: {}",
                id
        );

        Venue venue =
                findVenue(id);

        return venueMapper.toResponse(venue);
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<VenueResponse> getAllVenues() {

        log.debug(
                "Fetching all venues"
        );

        List<Venue> venues =
                venueRepository.findAll();

        return venueMapper.toResponseList(venues);
    }

    // UPDATE
    @Override
    @Transactional
    public VenueResponse updateVenue(
            Long id,
            VenueRequest request
    ) {

        log.info(
                "Update requested for venue id: {}",
                id
        );

        Venue venue =
                findVenue(id);

        venueMapper.updateEntity(
                request,
                venue
        );

        Venue updatedVenue =
                venueRepository.save(venue);

        log.info(
                "Venue updated successfully with id: {}",
                updatedVenue.getId()
        );

        return venueMapper.toResponse(updatedVenue);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteVenue(
            Long id
    ) {

        log.info(
                "Delete requested for venue id: {}",
                id
        );

        Venue venue =
                findVenue(id);

        venueRepository.delete(venue);

        log.info(
                "Venue deleted successfully with id: {}",
                id
        );
    }

    // -----------------------------
    // PRIVATE HELPER METHOD
    // -----------------------------

    private Venue findVenue(
            Long id
    ) {

        return venueRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Venue not found with id: {}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Venue not found with id: " + id
                    );
                });
    }
}