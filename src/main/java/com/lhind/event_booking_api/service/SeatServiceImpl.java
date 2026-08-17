package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.seat.SeatRequest;
import com.lhind.event_booking_api.dto.seat.SeatResponse;
import com.lhind.event_booking_api.entity.Seat;
import com.lhind.event_booking_api.entity.Venue;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.SeatMapper;
import com.lhind.event_booking_api.repository.SeatRepository;
import com.lhind.event_booking_api.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatServiceImpl implements SeatService {

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
    public SeatResponse createSeat(SeatRequest request) {

        Venue venue = findVenue(request);

        if (seatRepository.existsByVenueIdAndRowNumberAndSeatNumber(
                venue.getId(),
                request.getRowNumber(),
                request.getSeatNumber()
        )) {
            throw new DuplicateResourceException(
                    "Seat already exists in this venue"
            );
        }

        Seat seat = seatMapper.toEntity(request, venue);

        Seat savedSeat = seatRepository.save(seat);

        return seatMapper.toResponse(savedSeat);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public SeatResponse getSeatById(Long id) {

        Seat seat = findSeat(id);

        return seatMapper.toResponse(seat);
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> getAllSeats() {

        return seatMapper.toResponseList(
                seatRepository.findAll()
        );
    }

    // GET BY VENUE
    @Override
    @Transactional(readOnly = true)
    public List<SeatResponse> getSeatsByVenue(Long venueId) {

        if (!venueRepository.existsById(venueId)) {
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

        Seat seat = findSeat(id);

        Venue venue = findVenue(request);

        boolean seatChanged =
                !seat.getVenue().getId().equals(venue.getId())
                        || !seat.getRowNumber().equals(request.getRowNumber())
                        || !seat.getSeatNumber().equals(request.getSeatNumber());

        if (seatChanged
                && seatRepository.existsByVenueIdAndRowNumberAndSeatNumber(
                venue.getId(),
                request.getRowNumber(),
                request.getSeatNumber()
        )) {

            throw new DuplicateResourceException(
                    "Seat already exists in this venue"
            );
        }

        seatMapper.updateEntity(
                request,
                seat,
                venue
        );

        Seat updatedSeat = seatRepository.save(seat);

        return seatMapper.toResponse(updatedSeat);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteSeat(Long id) {

        Seat seat = findSeat(id);

        seatRepository.delete(seat);
    }

    // PRIVATE METHODS
    //Metoda private ndihmese qe perdoren brenda metodave te tjera dhe vetem brenda klases:
    //getSeatById()
    //updateSeat()
    //deleteSeat()


    private Seat findSeat(Long id) {

        return seatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat not found with id: " + id
                        )
                );
    }

    private Venue findVenue(SeatRequest request) {

        return venueRepository
                .findByVenueNameAndVenueCity(
                        request.getVenue().getVenueName(),
                        request.getVenue().getVenueCity()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Venue not found: "
                                        + request.getVenue().getVenueName()
                        )
                );
    }
}