package com.lhind.event_booking_api.service;
import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.BookingMapper;
import com.lhind.event_booking_api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final EventSeatRepository eventSeatRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            SeatRepository seatRepository,
            EventSeatRepository eventSeatRepository,
            BookingMapper bookingMapper
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.eventSeatRepository = eventSeatRepository;
        this.bookingMapper = bookingMapper;
    }

    // CREATE BOOKING
    //Kjo metode ul dhe avaible total Seat per cdo Seat qe kalon ne status Reserved
    @Override
    @Transactional
    public BookingResponse createBooking(
            Long userId,
            BookingRequest request
    ) {

        User user = findUser(userId);

        Event event = findEvent(request.getEventId());

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

        Booking booking = findBooking(bookingId);

        return bookingMapper.toResponse(booking);
    }

    // GET BY USER
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(
            Long userId
    ) {

        findUser(userId);

        return bookingMapper.toResponseList(
                bookingRepository.findByUserId(userId)
        );
    }

    // GET BY EVENT
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByEvent(
            Long eventId
    ) {

        findEvent(eventId);

        return bookingMapper.toResponseList(
                bookingRepository.findByEventId(eventId)
        );
    }

    // GET BY USER + STATUS
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserAndStatus(
            Long userId,
            BookingStatus status
    ) {

        findUser(userId);

        return bookingMapper.toResponseList(
                bookingRepository
                        .findByUserIdAndBookingStatus(
                                userId,
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

        Booking booking = findBooking(bookingId);

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
            Long bookingId,
            Long userId
    ) {

        Booking booking = findBooking(bookingId);

        validateOwnership(booking, userId);

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
         * Lejojm anulim vetem sa kohe seats
         * nuk jane bere SOLD.
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

        Event event = booking.getEvent();

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

        Booking booking = findBooking(bookingId);

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

    //
    // PRIVATE HELPER METHODS

    private Booking findBooking(Long bookingId) {

        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: "
                                        + bookingId
                        )
                );
    }

    private User findUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );
    }

    private Event findEvent(Long eventId) {

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
            Long userId
    ) {

        if (!booking.getUser()
                .getId()
                .equals(userId)) {

            throw new InvalidOperationException(
                    "You are not allowed to modify this booking"
            );
        }
    }

    private void validateDuplicateSeats(
            List<SeatSelectionRequest> seats
    ) {

        Set<String> uniqueSeats = new HashSet<>();

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