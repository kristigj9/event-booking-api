package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.seat.SeatRequest;
import com.lhind.event_booking_api.dto.seat.SeatResponse;
import com.lhind.event_booking_api.entity.Seat;
import com.lhind.event_booking_api.entity.Venue;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.SeatMapper;
import com.lhind.event_booking_api.repository.SeatRepository;
import com.lhind.event_booking_api.repository.VenueRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

    private static final Logger log =
            LogManager.getLogger(SeatServiceImpl.class);

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;
    private final SeatMapper seatMapper;

    public SeatServiceImpl(
            SeatRepository seatRepository,
            VenueRepository venueRepository,
            SeatMapper seatMapper
    ) {
        this.seatRepository = seatRepository;
        this.venueRepository = venueRepository;
        this.seatMapper = seatMapper;
    }

    // CREATE
    @Override
    @Transactional
    public SeatResponse createSeat(
            SeatRequest request
    ) {

        Venue venue =
                findVenue(request);

        log.info(
                "Creating seat {}-{} for venue id: {}",
                request.getRowNumber(),
                request.getSeatNumber(),
                venue.getId()
        );

        if (seatRepository
                .existsByVenueIdAndRowNumberAndSeatNumber(
                        venue.getId(),
                        request.getRowNumber(),
                        request.getSeatNumber()
                )) {

            log.warn(
                    "Seat creation rejected. Seat {}-{} already exists in venue id: {}",
                    request.getRowNumber(),
                    request.getSeatNumber(),
                    venue.getId()
            );

            throw new DuplicateResourceException(
                    "Seat already exists in this venue"
            );
        }

        Seat seat =
                seatMapper.toEntity(
                        request,
                        venue
                );

        Seat savedSeat =
                seatRepository.save(seat);

        log.info(
                "Seat created successfully with id: {}",
                savedSeat.getId()
        );

        return seatMapper.toResponse(savedSeat);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public SeatResponse getSeatById(
            Long id
    ) {

        log.debug(
                "Fetching seat by id: {}",
                id
        );

        Seat seat =
                findSeat(id);

        return seatMapper.toResponse(seat);
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> getAllSeats() {

        log.debug(
                "Fetching all seats"
        );

        return seatMapper.toResponseList(
                seatRepository.findAll()
        );
    }

    // GET BY VENUE
    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsByVenue(
            Long venueId
    ) {

        log.debug(
                "Fetching seats for venue id: {}",
                venueId
        );

        if (!venueRepository.existsById(venueId)) {

            log.warn(
                    "Venue not found with id: {} while fetching seats",
                    venueId
            );

            throw new ResourceNotFoundException(
                    "Venue not found with id: " + venueId
            );
        }

        return seatMapper.toResponseList(
                seatRepository.findByVenueId(venueId)
        );
    }

    // UPDATE
    @Override
    @Transactional
    public SeatResponse updateSeat(
            Long id,
            SeatRequest request
    ) {

        log.info(
                "Update requested for seat id: {}",
                id
        );

        Seat seat =
                findSeat(id);

        Venue venue =
                findVenue(request);

        boolean seatChanged =
                !seat.getVenue().getId().equals(venue.getId())
                        || !seat.getRowNumber().equals(request.getRowNumber())
                        || !seat.getSeatNumber().equals(request.getSeatNumber());

        if (seatChanged
                && seatRepository
                .existsByVenueIdAndRowNumberAndSeatNumber(
                        venue.getId(),
                        request.getRowNumber(),
                        request.getSeatNumber()
                )) {

            log.warn(
                    "Seat update rejected. Seat {}-{} already exists in venue id: {}",
                    request.getRowNumber(),
                    request.getSeatNumber(),
                    venue.getId()
            );

            throw new DuplicateResourceException(
                    "Seat already exists in this venue"
            );
        }

        seatMapper.updateEntity(
                request,
                seat,
                venue
        );

        Seat updatedSeat =
                seatRepository.save(seat);

        log.info(
                "Seat updated successfully with id: {}",
                updatedSeat.getId()
        );

        return seatMapper.toResponse(updatedSeat);
    }

    //DELETE
    @Override
    @Transactional
    public void deleteSeat(
            Long id
    ) {

        log.info(
                "Delete requested for seat id: {}",
                id
        );

        Seat seat =
                findSeat(id);

        if (!seat.getEventSeats().isEmpty()) {

            log.warn(
                    "Seat deletion rejected for seat id: {} because it is assigned to events",
                    id
            );

            throw new InvalidOperationException(
                    "Seat cannot be deleted because it is assigned to one or more events"
            );
        }

        seatRepository.delete(seat);

        log.info(
                "Seat deleted successfully with id: {}",
                id
        );
    }

    // -----------------------------
    // PRIVATE HELPER METHODS
    // -----------------------------

    private Seat findSeat(
            Long id
    ) {

        return seatRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Seat not found with id: {}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Seat not found with id: " + id
                    );
                });
    }

    private Venue findVenue(
            SeatRequest request
    ) {

        String venueName =
                request.getVenue().getVenueName();

        String venueCity =
                request.getVenue().getVenueCity();

        return venueRepository
                .findByVenueNameAndVenueCity(
                        venueName,
                        venueCity
                )
                .orElseThrow(() -> {

                    log.warn(
                            "Venue not found with name '{}' and city '{}'",
                            venueName,
                            venueCity
                    );

                    return new ResourceNotFoundException(
                            "Venue not found: "
                                    + venueName
                    );
                });
    }
}