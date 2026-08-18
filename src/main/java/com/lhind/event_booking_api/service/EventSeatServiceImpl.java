package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.eventseat.EventSeatRequest;
import com.lhind.event_booking_api.dto.eventseat.EventSeatResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.EventSeatMapper;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.EventSeatRepository;
import com.lhind.event_booking_api.repository.SeatRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EventSeatServiceImpl implements EventSeatService {

    private final EventSeatRepository eventSeatRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final EventSeatMapper eventSeatMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public EventSeatServiceImpl(
            EventSeatRepository eventSeatRepository,
            EventRepository eventRepository,
            SeatRepository seatRepository,
            EventSeatMapper eventSeatMapper,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.eventSeatRepository = eventSeatRepository;
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.eventSeatMapper = eventSeatMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    @Override
    @Transactional
    public EventSeatResponse createEventSeat(
            Long eventId,
            EventSeatRequest request
    ) {

        Event event = findEvent(eventId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validateOwnership(
                event,
                currentUser
        );

        Seat seat = seatRepository
                .findByVenueIdAndRowNumberAndSeatNumber(
                        event.getVenue().getId(),
                        request.getSeat().getRowNumber(),
                        request.getSeat().getSeatNumber()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat not found in event venue"
                        )
                );

        if (eventSeatRepository
                .findByEventIdAndSeatId(
                        eventId,
                        seat.getId()
                )
                .isPresent()) {

            throw new DuplicateResourceException(
                    "Seat is already assigned to this event"
            );
        }

        EventSeat eventSeat =
                EventSeat.builder()
                        .event(event)
                        .seat(seat)
                        .statusSeat(StatusSeat.AVAILABLE)
                        .priceSeat(request.getPriceSeat())
                        .build();

        EventSeat savedEventSeat =
                eventSeatRepository.save(eventSeat);

        return eventSeatMapper.toResponse(savedEventSeat);
    }

    @Override
    @Transactional(readOnly = true)
    public EventSeatResponse getEventSeatById(
            Long id
    ) {

        return eventSeatMapper.toResponse(
                findEventSeat(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventSeatResponse> getSeatsByEvent(
            Long eventId
    ) {

        findEvent(eventId);

        return eventSeatMapper.toResponseList(
                eventSeatRepository.findByEventId(eventId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventSeatResponse> getSeatsByEventAndStatus(
            Long eventId,
            StatusSeat statusSeat
    ) {

        findEvent(eventId);

        return eventSeatMapper.toResponseList(
                eventSeatRepository
                        .findByEventIdAndStatusSeat(
                                eventId,
                                statusSeat
                        )
        );
    }

    @Override
    @Transactional
    public EventSeatResponse updatePrice(
            Long eventSeatId,
            BigDecimal priceSeat
    ) {

        EventSeat eventSeat =
                findEventSeat(eventSeatId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validateOwnership(
                eventSeat.getEvent(),
                currentUser
        );

        if (priceSeat == null
                || priceSeat.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidOperationException(
                    "Seat price must be greater than 0"
            );
        }

        eventSeat.setPriceSeat(priceSeat);

        return eventSeatMapper.toResponse(
                eventSeatRepository.save(eventSeat)
        );
    }

    @Override
    @Transactional
    public void deleteEventSeat(
            Long eventSeatId
    ) {

        EventSeat eventSeat =
                findEventSeat(eventSeatId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validateOwnership(
                eventSeat.getEvent(),
                currentUser
        );

        /*
         * Nuk lejojmë fshirjen e një seat-i
         * që është RESERVED ose SOLD.
         */
        if (eventSeat.getStatusSeat()
                != StatusSeat.AVAILABLE) {

            throw new InvalidOperationException(
                    "Only available event seats can be deleted"
            );
        }

        eventSeatRepository.delete(eventSeat);
    }

    // -----------------------------
    // PRIVATE HELPER METHODS
    // -----------------------------

    private Event findEvent(
            Long eventId
    ) {

        return eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: "
                                        + eventId
                        )
                );
    }

    private EventSeat findEventSeat(
            Long id
    ) {

        return eventSeatRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event seat not found with id: "
                                        + id
                        )
                );
    }

    private void validateOwnership(
            Event event,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        boolean isOwner =
                event.getOrganizer()
                        .getId()
                        .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {

            throw new InvalidOperationException(
                    "You are not allowed to modify seats for this event"
            );
        }
    }
}