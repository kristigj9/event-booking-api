package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.BookingMapper;
import com.lhind.event_booking_api.repository.BookingRepository;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.EventSeatRepository;
import com.lhind.event_booking_api.repository.SeatRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log =
            LogManager.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final EventSeatRepository eventSeatRepository;
    private final BookingMapper bookingMapper;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificationService notificationService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            EventRepository eventRepository,
            SeatRepository seatRepository,
            EventSeatRepository eventSeatRepository,
            BookingMapper bookingMapper,
            AuthenticatedUserService authenticatedUserService,
            NotificationService notificationService
    ) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.eventSeatRepository = eventSeatRepository;
        this.bookingMapper = bookingMapper;
        this.authenticatedUserService = authenticatedUserService;
        this.notificationService = notificationService;
    }

    // CREATE BOOKING
    @Override
    @Transactional
    public BookingResponse createBooking(
            BookingRequest request
    ) {

        User user =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Creating booking for user id: {} and event id: {}",
                user.getId(),
                request.getEventId()
        );

        Event event =
                findEvent(request.getEventId());

        if (event.getEventStatus()
                != EventStatus.PUBLISHED) {

            log.warn(
                    "Booking rejected for event id: {} because event status is: {}",
                    event.getId(),
                    event.getEventStatus()
            );

            throw new InvalidOperationException(
                    "Bookings can only be created for published events"
            );
        }

        if (!event.getEventStartDateTime()
                .isAfter(LocalDateTime.now())) {

            log.warn(
                    "Booking rejected for event id: {} because event has already started",
                    event.getId()
            );

            throw new InvalidOperationException(
                    "Booking cannot be created after the event has started"
            );
        }

        if (event.getEventAvailableSeats()
                < request.getSeats().size()) {

            log.warn(
                    "Booking rejected for event id: {}. Requested seats: {}, available seats: {}",
                    event.getId(),
                    request.getSeats().size(),
                    event.getEventAvailableSeats()
            );

            throw new InvalidOperationException(
                    "Not enough available seats for this event"
            );
        }

        validateDuplicateSeats(
                request.getSeats()
        );

        Booking booking =
                bookingMapper.toEntity(
                        request,
                        user,
                        event
                );

        for (SeatSelectionRequest seatRequest
                : request.getSeats()) {

            Seat seat =
                    findSeat(
                            event,
                            seatRequest
                    );

            EventSeat eventSeat =
                    findEventSeat(
                            event.getId(),
                            seat.getId()
                    );

            if (eventSeat.getStatusSeat()
                    != StatusSeat.AVAILABLE) {

                log.warn(
                        "Seat {}-{} is not available for event id: {}",
                        seat.getRowNumber(),
                        seat.getSeatNumber(),
                        event.getId()
                );

                throw new InvalidOperationException(
                        "Seat "
                                + seat.getRowNumber()
                                + "-"
                                + seat.getSeatNumber()
                                + " is not available"
                );
            }

            BookingSeat bookingSeat =
                    BookingSeat.builder()
                            .booking(booking)
                            .eventSeat(eventSeat)
                            .build();

            booking.getBookingSeats()
                    .add(bookingSeat);

            eventSeat.setStatusSeat(
                    StatusSeat.RESERVED
            );

            log.debug(
                    "Seat {}-{} reserved for event id: {}",
                    seat.getRowNumber(),
                    seat.getSeatNumber(),
                    event.getId()
            );
        }

        event.setEventAvailableSeats(
                event.getEventAvailableSeats()
                        - request.getSeats().size()
        );

        Booking savedBooking =
                bookingRepository.save(booking);

        log.info(
                "Booking created successfully with id: {} for user id: {}",
                savedBooking.getId(),
                user.getId()
        );

        return bookingMapper.toResponse(
                savedBooking
        );
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(
            Long bookingId
    ) {

        log.debug(
                "Fetching booking by id: {}",
                bookingId
        );

        Booking booking =
                findBooking(bookingId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validateOwnership(
                booking,
                currentUser
        );

        return bookingMapper.toResponse(
                booking
        );
    }

    // GET BOOKINGS OF CURRENT USER
    // GET BOOKINGS OF CURRENT USER - NATIVE QUERY
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.debug(
                "Fetching bookings for user id: {} using native query",
                currentUser.getId()
        );

        return bookingMapper.toResponseList(
                bookingRepository.findBookingsByUserNative(
                        currentUser.getId()
                )
        );
    }
    // GET BOOKINGS BY EVENT
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByEvent(
            Long eventId
    ) {

        Event event =
                findEvent(eventId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.debug(
                "Fetching bookings for event id: {} by user id: {}",
                eventId,
                currentUser.getId()
        );

        validateEventOwnership(
                event,
                currentUser
        );

        return bookingMapper.toResponseList(
                bookingRepository.findByEventId(
                        eventId
                )
        );
    }

    // GET CURRENT USER BOOKINGS BY STATUS
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookingsByStatus(
            BookingStatus status
    ) {

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.debug(
                "Fetching bookings for user id: {} with status: {}",
                currentUser.getId(),
                status
        );

        return bookingMapper.toResponseList(
                bookingRepository
                        .findByUserIdAndBookingStatus(
                                currentUser.getId(),
                                status
                        )
        );
    }

    // CONFIRM
    @Override
    @Transactional
    public BookingResponse confirmBooking(
            Long bookingId
    ) {

        Booking booking =
                findBooking(bookingId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Confirm requested for booking id: {} by user id: {}",
                bookingId,
                currentUser.getId()
        );

        validateEventOwnership(
                booking.getEvent(),
                currentUser
        );

        if (booking.getBookingStatus()
                != BookingStatus.PENDING) {

            log.warn(
                    "Booking id: {} cannot be confirmed because current status is: {}",
                    bookingId,
                    booking.getBookingStatus()
            );

            throw new InvalidOperationException(
                    "Only pending bookings can be confirmed"
            );
        }

        for (BookingSeat bookingSeat
                : booking.getBookingSeats()) {

            EventSeat eventSeat =
                    bookingSeat.getEventSeat();

            if (eventSeat.getStatusSeat()
                    != StatusSeat.RESERVED) {

                log.warn(
                        "Booking id: {} contains event seat id: {} with invalid status: {}",
                        bookingId,
                        eventSeat.getId(),
                        eventSeat.getStatusSeat()
                );

                throw new InvalidOperationException(
                        "Booking contains a seat that is not reserved"
                );
            }

            eventSeat.setStatusSeat(
                    StatusSeat.SOLD
            );
        }

        booking.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        Booking savedBooking =
                bookingRepository.save(booking);

        log.info(
                "Booking confirmed successfully with id: {}",
                savedBooking.getId()
        );

        notificationService.createNotification(
                savedBooking.getUser(),
                savedBooking.getEvent(),
                savedBooking,
                NotificationType.BOOKING_CONFIRMED,
                "Your booking for event "
                        + savedBooking.getEvent().getEventName()
                        + " has been confirmed"
        );

        return bookingMapper.toResponse(
                savedBooking
        );
    }

    // CANCEL
    @Override
    @Transactional
    public BookingResponse cancelBooking(
            Long bookingId
    ) {

        Booking booking =
                findBooking(bookingId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Cancellation requested for booking id: {} by user id: {}",
                bookingId,
                currentUser.getId()
        );

        validateOwnership(
                booking,
                currentUser
        );

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

            log.warn(
                    "Booking id: {} is already cancelled",
                    bookingId
            );

            throw new InvalidOperationException(
                    "Booking is already cancelled"
            );
        }

        if (booking.getBookingStatus()
                == BookingStatus.COMPLETED) {

            log.warn(
                    "Completed booking id: {} cannot be cancelled",
                    bookingId
            );

            throw new InvalidOperationException(
                    "Completed booking cannot be cancelled"
            );
        }

        for (BookingSeat bookingSeat
                : booking.getBookingSeats()) {

            EventSeat eventSeat =
                    bookingSeat.getEventSeat();

            if (eventSeat.getStatusSeat()
                    == StatusSeat.RESERVED
                    || eventSeat.getStatusSeat()
                    == StatusSeat.SOLD) {

                eventSeat.setStatusSeat(
                        StatusSeat.AVAILABLE
                );
            }
        }

        Event event =
                booking.getEvent();

        event.setEventAvailableSeats(
                event.getEventAvailableSeats()
                        + booking.getSeatsBooked()
        );

        booking.setBookingStatus(
                BookingStatus.CANCELLED
        );

        Booking savedBooking =
                bookingRepository.save(booking);

        log.info(
                "Booking cancelled successfully with id: {}",
                savedBooking.getId()
        );

        notificationService.createNotification(
                savedBooking.getUser(),
                savedBooking.getEvent(),
                savedBooking,
                NotificationType.BOOKING_CANCELLED,
                "Your booking for event "
                        + savedBooking.getEvent().getEventName()
                        + " has been cancelled"
        );

        return bookingMapper.toResponse(
                savedBooking
        );
    }

    // COMPLETE
    @Override
    @Transactional
    public BookingResponse completeBooking(
            Long bookingId
    ) {

        Booking booking =
                findBooking(bookingId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Complete requested for booking id: {} by user id: {}",
                bookingId,
                currentUser.getId()
        );

        validateEventOwnership(
                booking.getEvent(),
                currentUser
        );

        if (booking.getBookingStatus()
                != BookingStatus.CONFIRMED) {

            log.warn(
                    "Booking id: {} cannot be completed because current status is: {}",
                    bookingId,
                    booking.getBookingStatus()
            );

            throw new InvalidOperationException(
                    "Only confirmed bookings can be completed"
            );
        }

        booking.setBookingStatus(
                BookingStatus.COMPLETED
        );

        Booking savedBooking =
                bookingRepository.save(booking);

        log.info(
                "Booking completed successfully with id: {}",
                savedBooking.getId()
        );

        notificationService.createNotification(
                savedBooking.getUser(),
                savedBooking.getEvent(),
                savedBooking,
                NotificationType.BOOKING_COMPLETED,
                "Your booking for event "
                        + savedBooking.getEvent().getEventName()
                        + " has been completed"
        );

        return bookingMapper.toResponse(
                savedBooking
        );
    }

    // --------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------

    private Booking findBooking(
            Long bookingId
    ) {

        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> {

                    log.warn(
                            "Booking not found with id: {}",
                            bookingId
                    );

                    return new ResourceNotFoundException(
                            "Booking not found with id: "
                                    + bookingId
                    );
                });
    }

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

    private Seat findSeat(
            Event event,
            SeatSelectionRequest request
    ) {

        return seatRepository
                .findByVenueIdAndRowNumberAndSeatNumber(
                        event.getVenue().getId(),
                        request.getRowNumber(),
                        request.getSeatNumber()
                )
                .orElseThrow(() -> {

                    log.warn(
                            "Seat not found in venue id: {}. Seat: {}-{}",
                            event.getVenue().getId(),
                            request.getRowNumber(),
                            request.getSeatNumber()
                    );

                    return new ResourceNotFoundException(
                            "Seat not found: "
                                    + request.getRowNumber()
                                    + "-"
                                    + request.getSeatNumber()
                    );
                });
    }

    private EventSeat findEventSeat(
            Long eventId,
            Long seatId
    ) {

        return eventSeatRepository
                .findByEventIdAndSeatId(
                        eventId,
                        seatId
                )
                .orElseThrow(() -> {

                    log.warn(
                            "Seat id: {} is not assigned to event id: {}",
                            seatId,
                            eventId
                    );

                    return new ResourceNotFoundException(
                            "Seat is not assigned to this event"
                    );
                });
    }

    private void validateOwnership(
            Booking booking,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        boolean isOwner =
                booking.getUser()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        if (!isAdmin && !isOwner) {

            log.warn(
                    "Unauthorized booking access attempt. Booking id: {}, user id: {}",
                    booking.getId(),
                    currentUser.getId()
            );

            throw new InvalidOperationException(
                    "You are not allowed to access or modify this booking"
            );
        }

        log.debug(
                "Booking ownership validation successful. Booking id: {}, user id: {}, admin: {}",
                booking.getId(),
                currentUser.getId(),
                isAdmin
        );
    }

    private void validateEventOwnership(
            Event event,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        boolean isOrganizer =
                event.getOrganizer()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        if (!isAdmin && !isOrganizer) {

            log.warn(
                    "Unauthorized booking management attempt. Event id: {}, user id: {}",
                    event.getId(),
                    currentUser.getId()
            );

            throw new InvalidOperationException(
                    "You are not allowed to manage bookings for this event"
            );
        }

        log.debug(
                "Event booking management authorization successful. Event id: {}, user id: {}, admin: {}",
                event.getId(),
                currentUser.getId(),
                isAdmin
        );
    }

    private void validateDuplicateSeats(
            List<SeatSelectionRequest> seats
    ) {

        Set<String> uniqueSeats =
                new HashSet<>();

        for (SeatSelectionRequest seat : seats) {

            String key =
                    seat.getRowNumber()
                            + "-"
                            + seat.getSeatNumber();

            if (!uniqueSeats.add(key)) {

                log.warn(
                        "Duplicate seat detected in booking request: {}",
                        key
                );

                throw new InvalidOperationException(
                        "The same seat cannot be selected more than once"
                );
            }
        }
    }
}