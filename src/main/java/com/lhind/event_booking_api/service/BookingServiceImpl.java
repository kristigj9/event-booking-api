package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.BookingMapper;
import com.lhind.event_booking_api.repository.*;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final EventSeatRepository eventSeatRepository;
    private final BookingMapper bookingMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            EventRepository eventRepository,
            SeatRepository seatRepository,
            EventSeatRepository eventSeatRepository,
            BookingMapper bookingMapper,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.eventSeatRepository = eventSeatRepository;
        this.bookingMapper = bookingMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    // CREATE BOOKING
    // Ul eventAvailableSeats për çdo Seat që kalon në RESERVED
    @Override
    @Transactional
    public BookingResponse createBooking(
            BookingRequest request
    ) {

        User user =
                authenticatedUserService.getCurrentUser();

        Event event =
                findEvent(request.getEventId());

        if (event.getEventAvailableSeats()
                < request.getSeats().size()) {

            throw new InvalidOperationException(
                    "Not enough available seats for this event"
            );
        }

        validateDuplicateSeats(request.getSeats());

        Booking booking = bookingMapper.toEntity(
                request,
                user,
                event
        );

        for (SeatSelectionRequest seatRequest
                : request.getSeats()) {

            Seat seat = findSeat(
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
        }

        event.setEventAvailableSeats(
                event.getEventAvailableSeats()
                        - request.getSeats().size()
        );

        Booking savedBooking =
                bookingRepository.save(booking);

        return bookingMapper.toResponse(savedBooking);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(
            Long bookingId
    ) {

        Booking booking =
                findBooking(bookingId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validateOwnership(booking, currentUser);

        return bookingMapper.toResponse(booking);
    }

    // GET BOOKINGS OF CURRENT USER
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings() {

        User currentUser =
                authenticatedUserService.getCurrentUser();

        return bookingMapper.toResponseList(
                bookingRepository.findByUserId(
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

        validateEventOwnership(
                event,
                currentUser
        );

        return bookingMapper.toResponseList(
                bookingRepository.findByEventId(eventId)
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

        validateEventOwnership(
                booking.getEvent(),
                currentUser
        );

        if (booking.getBookingStatus()
                != BookingStatus.PENDING) {

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

        return bookingMapper.toResponse(
                bookingRepository.save(booking)
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

        validateOwnership(
                booking,
                currentUser
        );

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

            throw new InvalidOperationException(
                    "Booking is already cancelled"
            );
        }

        if (booking.getBookingStatus()
                == BookingStatus.COMPLETED) {

            throw new InvalidOperationException(
                    "Completed booking cannot be cancelled"
            );
        }

        /*
         * Lejojmë anulim vetëm sa kohë seats
         * nuk janë bërë SOLD.
         */
        for (BookingSeat bookingSeat
                : booking.getBookingSeats()) {

            EventSeat eventSeat =
                    bookingSeat.getEventSeat();

            if (eventSeat.getStatusSeat()
                    == StatusSeat.SOLD) {

                throw new InvalidOperationException(
                        "Confirmed seats cannot be released"
                );
            }

            eventSeat.setStatusSeat(
                    StatusSeat.AVAILABLE
            );
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

        return bookingMapper.toResponse(
                bookingRepository.save(booking)
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

        validateEventOwnership(
                booking.getEvent(),
                currentUser
        );

        if (booking.getBookingStatus()
                != BookingStatus.CONFIRMED) {

            throw new InvalidOperationException(
                    "Only confirmed bookings can be completed"
            );
        }

        booking.setBookingStatus(
                BookingStatus.COMPLETED
        );

        return bookingMapper.toResponse(
                bookingRepository.save(booking)
        );
    }


    // --------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------

    private Booking findBooking(
            Long bookingId
    ) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: "
                                        + bookingId
                        )
                );
    }

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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat not found: "
                                        + request.getRowNumber()
                                        + "-"
                                        + request.getSeatNumber()
                        )
                );
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
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat is not assigned to this event"
                        )
                );
    }

    private void validateOwnership(
            Booking booking,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        boolean isOwner =
                booking.getUser()
                        .getId()
                        .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {

            throw new InvalidOperationException(
                    "You are not allowed to access or modify this booking"
            );
        }
    }

    private void validateEventOwnership(
            Event event,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        boolean isOrganizer =
                event.getOrganizer()
                        .getId()
                        .equals(currentUser.getId());

        if (!isAdmin && !isOrganizer) {

            throw new InvalidOperationException(
                    "You are not allowed to manage bookings for this event"
            );
        }
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

                throw new InvalidOperationException(
                        "The same seat cannot be selected more than once"
                );
            }
        }
    }
}