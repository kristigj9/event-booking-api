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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EventSeatServiceImpl implements EventSeatService {

    private static final Logger log =
            LogManager.getLogger(EventSeatServiceImpl.class);

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

    // CREATE
    @Override
    @Transactional
    public EventSeatResponse createEventSeat(
            Long eventId,
            EventSeatRequest request
    ) {

        Event event =
                findEvent(eventId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Creating event seat for event id: {} by user id: {}",
                eventId,
                currentUser.getId()
        );

        validateOwnership(
                event,
                currentUser
        );

        Seat seat =
                seatRepository
                        .findByVenueIdAndRowNumberAndSeatNumber(
                                event.getVenue().getId(),
                                request.getSeat().getRowNumber(),
                                request.getSeat().getSeatNumber()
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "Seat not found in venue id: {}. Seat: {}-{}",
                                    event.getVenue().getId(),
                                    request.getSeat().getRowNumber(),
                                    request.getSeat().getSeatNumber()
                            );

                            return new ResourceNotFoundException(
                                    "Seat not found in event venue"
                            );
                        });

        if (eventSeatRepository
                .findByEventIdAndSeatId(
                        eventId,
                        seat.getId()
                )
                .isPresent()) {

            log.warn(
                    "Duplicate event seat assignment. Event id: {}, seat id: {}",
                    eventId,
                    seat.getId()
            );

            throw new DuplicateResourceException(
                    "Seat is already assigned to this event"
            );
        }

        EventSeat eventSeat =
                EventSeat.builder()
                        .event(event)
                        .seat(seat)
                        .priceSeat(
                                request.getPriceSeat()
                        )
                        .build();

        EventSeat savedEventSeat =
                eventSeatRepository.save(eventSeat);

        log.info(
                "Event seat created successfully with id: {} for event id: {}",
                savedEventSeat.getId(),
                eventId
        );

        return eventSeatMapper.toResponse(
                savedEventSeat
        );
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public EventSeatResponse getEventSeatById(
            Long id
    ) {

        log.debug(
                "Fetching event seat by id: {}",
                id
        );

        return eventSeatMapper.toResponse(
                findEventSeat(id)
        );
    }

    // GET BY EVENT
    @Override
    @Transactional(readOnly = true)
    public List<EventSeatResponse> getSeatsByEvent(
            Long eventId
    ) {

        log.debug(
                "Fetching seats for event id: {}",
                eventId
        );

        findEvent(eventId);

        return eventSeatMapper.toResponseList(
                eventSeatRepository.findByEventId(
                        eventId
                )
        );
    }

    // GET BY EVENT AND STATUS
    @Override
    @Transactional(readOnly = true)
    public List<EventSeatResponse> getSeatsByEventAndStatus(
            Long eventId,
            StatusSeat statusSeat
    ) {

        log.debug(
                "Fetching seats for event id: {} with status: {}",
                eventId,
                statusSeat
        );

        findEvent(eventId);

        return eventSeatMapper.toResponseList(
                eventSeatRepository
                        .findByEventIdAndStatusSeat(
                                eventId,
                                statusSeat
                        )
        );
    }

    // UPDATE PRICE
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

        log.info(
                "Price update requested for event seat id: {} by user id: {}",
                eventSeatId,
                currentUser.getId()
        );

        validateOwnership(
                eventSeat.getEvent(),
                currentUser
        );

        if (priceSeat == null
                || priceSeat.compareTo(BigDecimal.ZERO) <= 0) {

            log.warn(
                    "Invalid price update for event seat id: {}. Requested price: {}",
                    eventSeatId,
                    priceSeat
            );

            throw new InvalidOperationException(
                    "Seat price must be greater than 0"
            );
        }

        eventSeat.setPriceSeat(
                priceSeat
        );

        EventSeat updatedEventSeat =
                eventSeatRepository.save(eventSeat);

        log.info(
                "Event seat price updated successfully. Event seat id: {}, new price: {}",
                eventSeatId,
                priceSeat
        );

        return eventSeatMapper.toResponse(
                updatedEventSeat
        );
    }

    // DELETE
    @Override
    @Transactional
    public void deleteEventSeat(
            Long eventSeatId
    ) {

        EventSeat eventSeat =
                findEventSeat(eventSeatId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Delete requested for event seat id: {} by user id: {}",
                eventSeatId,
                currentUser.getId()
        );

        validateOwnership(
                eventSeat.getEvent(),
                currentUser
        );

        if (eventSeat.getStatusSeat()
                != StatusSeat.AVAILABLE) {

            log.warn(
                    "Event seat id: {} cannot be deleted because current status is: {}",
                    eventSeatId,
                    eventSeat.getStatusSeat()
            );

            throw new InvalidOperationException(
                    "Only available event seats can be deleted"
            );
        }

        if (!eventSeat.getBookingSeats().isEmpty()) {

            log.warn(
                    "Event seat id: {} cannot be deleted because it is referenced by bookings",
                    eventSeatId
            );

            throw new InvalidOperationException(
                    "Event seat cannot be deleted because it is referenced by bookings"
            );
        }

        eventSeatRepository.delete(
                eventSeat
        );

        log.info(
                "Event seat deleted successfully with id: {}",
                eventSeatId
        );
    }

    // PRIVATE HELPER METHODS

    private Event findEvent(
            Long eventId
    ) {

        return eventRepository
                .findById(eventId)
                .orElseThrow(() -> {

                    log.warn(
                            "Event not found with id: {}",
                            eventId
                    );

                    return new ResourceNotFoundException(
                            "Event not found with id: "
                                    + eventId
                    );
                });
    }

    private EventSeat findEventSeat(
            Long id
    ) {

        return eventSeatRepository
                .findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Event seat not found with id: {}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Event seat not found with id: "
                                    + id
                    );
                });
    }

    private void validateOwnership(
            Event event,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        boolean isOwner =
                event.getOrganizer()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        if (!isAdmin && !isOwner) {

            log.warn(
                    "Unauthorized event seat modification attempt. Event id: {}, user id: {}",
                    event.getId(),
                    currentUser.getId()
            );

            throw new InvalidOperationException(
                    "You are not allowed to modify seats for this event"
            );
        }

        log.debug(
                "Event seat ownership validation successful. Event id: {}, user id: {}, admin: {}",
                event.getId(),
                currentUser.getId(),
                isAdmin
        );
    }
}