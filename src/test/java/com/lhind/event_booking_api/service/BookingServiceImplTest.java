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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private EventSeatRepository eventSeatRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User organizer;
    private Venue venue;
    private Event event;
    private Seat seat;
    private EventSeat eventSeat;
    private Booking booking;
    private BookingRequest request;
    private BookingResponse response;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.USER)
                .build();

        organizer = User.builder()
                .id(2L)
                .firstName("Test")
                .lastName("Organizer")
                .email("organizer@test.com")
                .role(Role.ORGANIZER)
                .build();

        venue = Venue.builder()
                .id(1L)
                .venueName("Tirana Arena")
                .venueAddress("Tirana")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        event = Event.builder()
                .id(1L)
                .eventName("Music Event")
                .eventTotalSeats(100)
                .eventAvailableSeats(100)
                .eventStatus(EventStatus.PUBLISHED)
                .venue(venue)
                .organizer(organizer)
                .build();

        seat = Seat.builder()
                .id(1L)
                .rowNumber("A")
                .seatNumber(1)
                .venue(venue)
                .build();

        eventSeat = EventSeat.builder()
                .id(1L)
                .event(event)
                .seat(seat)
                .statusSeat(StatusSeat.AVAILABLE)
                .build();

        SeatSelectionRequest seatRequest =
                new SeatSelectionRequest();

        seatRequest.setRowNumber("A");
        seatRequest.setSeatNumber(1);

        request = new BookingRequest();
        request.setEventId(1L);
        request.setSeats(List.of(seatRequest));

        booking = Booking.builder()
                .id(1L)
                .user(user)
                .event(event)
                .bookingStatus(BookingStatus.PENDING)
                .seatsBooked(1)
                .bookingSeats(new ArrayList<>())
                .build();

        response = BookingResponse.builder()
                .id(1L)
                .bookingStatus(BookingStatus.PENDING)
                .build();
    }

    @Test
    void createBooking_shouldCreateBookingSuccessfully() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(bookingMapper.toEntity(
                request,
                user,
                event
        )).thenReturn(booking);

        when(seatRepository
                .findByVenueIdAndRowNumberAndSeatNumber(
                        1L,
                        "A",
                        1
                ))
                .thenReturn(Optional.of(seat));

        when(eventSeatRepository
                .findByEventIdAndSeatId(
                        1L,
                        1L
                ))
                .thenReturn(Optional.of(eventSeat));

        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(bookingMapper.toResponse(any(Booking.class)))
                .thenReturn(response);

        BookingResponse result =
                bookingService.createBooking(request);

        assertNotNull(result);

        assertEquals(
                BookingStatus.PENDING,
                result.getBookingStatus()
        );

        assertEquals(
                StatusSeat.RESERVED,
                eventSeat.getStatusSeat()
        );

        assertEquals(
                99,
                event.getEventAvailableSeats()
        );

        assertEquals(
                1,
                booking.getBookingSeats().size()
        );

        verify(bookingRepository, times(1))
                .save(booking);
    }

    @Test
    void createBooking_shouldThrowExceptionWhenNotEnoughSeats() {

        event.setEventAvailableSeats(0);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.createBooking(request)
                );

        assertEquals(
                "Not enough available seats for this event",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    //DUPLICATE SEAT

    @Test
    void createBooking_shouldThrowExceptionWhenSameSeatSelectedTwice() {

        SeatSelectionRequest seat1 =
                new SeatSelectionRequest();

        seat1.setRowNumber("A");
        seat1.setSeatNumber(1);

        SeatSelectionRequest seat2 =
                new SeatSelectionRequest();

        seat2.setRowNumber("A");
        seat2.setSeatNumber(1);

        request.setSeats(
                List.of(seat1, seat2)
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.createBooking(request)
                );

        assertEquals(
                "The same seat cannot be selected more than once",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    //SEAT NUK EKZISTON
    @Test
    void createBooking_shouldThrowExceptionWhenSeatNotFound() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(bookingMapper.toEntity(
                request,
                user,
                event
        )).thenReturn(booking);

        when(seatRepository
                .findByVenueIdAndRowNumberAndSeatNumber(
                        1L,
                        "A",
                        1
                ))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookingService.createBooking(request)
                );

        assertEquals(
                "Seat not found: A-1",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    //SEAT EKZISTON, por nuk eshte lidhur me eventin

    @Test
    void createBooking_shouldThrowExceptionWhenSeatNotAssignedToEvent() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(bookingMapper.toEntity(
                request,
                user,
                event
        )).thenReturn(booking);

        when(seatRepository
                .findByVenueIdAndRowNumberAndSeatNumber(
                        1L,
                        "A",
                        1
                ))
                .thenReturn(Optional.of(seat));

        when(eventSeatRepository
                .findByEventIdAndSeatId(
                        1L,
                        1L
                ))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> bookingService.createBooking(request)
                );

        assertEquals(
                "Seat is not assigned to this event",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }
    //Dhe SEAT EKZISTON, por eshte RESERVED

    @Test
    void createBooking_shouldThrowExceptionWhenSeatIsNotAvailable() {

        eventSeat.setStatusSeat(
                StatusSeat.RESERVED
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(bookingMapper.toEntity(
                request,
                user,
                event
        )).thenReturn(booking);

        when(seatRepository
                .findByVenueIdAndRowNumberAndSeatNumber(
                        1L,
                        "A",
                        1
                ))
                .thenReturn(Optional.of(seat));

        when(eventSeatRepository
                .findByEventIdAndSeatId(
                        1L,
                        1L
                ))
                .thenReturn(Optional.of(eventSeat));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.createBooking(request)
                );

        assertEquals(
                "Seat A-1 is not available",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    //CONFIRM BOOKING
    @Test
    void confirmBooking_shouldConfirmBookingSuccessfully() {

        BookingSeat bookingSeat = BookingSeat.builder()
                .booking(booking)
                .eventSeat(eventSeat)
                .build();

        booking.setBookingSeats(
                new ArrayList<>(List.of(bookingSeat))
        );

        booking.setBookingStatus(
                BookingStatus.PENDING
        );

        eventSeat.setStatusSeat(
                StatusSeat.RESERVED
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        response.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.confirmBooking(1L);

        assertNotNull(result);

        assertEquals(
                BookingStatus.CONFIRMED,
                booking.getBookingStatus()
        );

        assertEquals(
                StatusSeat.SOLD,
                eventSeat.getStatusSeat()
        );

        verify(bookingRepository, times(1))
                .save(booking);
    }

//    Testojme qe booking jo PENDING nuk mund te konfirmohet

    @Test
    void confirmBooking_shouldThrowExceptionWhenBookingIsNotPending() {

        booking.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.confirmBooking(1L)
                );

        assertEquals(
                "Only pending bookings can be confirmed",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

//    Test per seat qe nuk eshte RESERVED:

    @Test
    void confirmBooking_shouldThrowExceptionWhenSeatIsNotReserved() {

        BookingSeat bookingSeat = BookingSeat.builder()
                .booking(booking)
                .eventSeat(eventSeat)
                .build();

        booking.setBookingSeats(
                new ArrayList<>(List.of(bookingSeat))
        );

        booking.setBookingStatus(
                BookingStatus.PENDING
        );

        eventSeat.setStatusSeat(
                StatusSeat.AVAILABLE
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.confirmBooking(1L)
                );

        assertEquals(
                "Booking contains a seat that is not reserved",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }
//    Nje OWNERSHIP test: nje organizer tjeter nuk  Konfermon booking-un e dikujt tjeter

    @Test
    void confirmBooking_shouldThrowExceptionWhenOrganizerIsNotEventOwner() {

        User anotherOrganizer = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("Organizer")
                .email("other.organizer@test.com")
                .role(Role.ORGANIZER)
                .build();

        booking.setBookingStatus(
                BookingStatus.PENDING
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherOrganizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.confirmBooking(1L)
                );

        assertEquals(
                "You are not allowed to manage bookings for this event",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

    // Cancel BOOKING me sukses

    @Test
    void cancelBooking_shouldCancelBookingSuccessfully() {

        BookingSeat bookingSeat = BookingSeat.builder()
                .booking(booking)
                .eventSeat(eventSeat)
                .build();

        booking.setBookingSeats(
                new ArrayList<>(List.of(bookingSeat))
        );

        booking.setBookingStatus(
                BookingStatus.PENDING
        );

        booking.setSeatsBooked(1);

        event.setEventAvailableSeats(99);

        eventSeat.setStatusSeat(
                StatusSeat.RESERVED
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        response.setBookingStatus(
                BookingStatus.CANCELLED
        );

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.cancelBooking(1L);

        assertNotNull(result);

        assertEquals(
                BookingStatus.CANCELLED,
                booking.getBookingStatus()
        );

        assertEquals(
                StatusSeat.AVAILABLE,
                eventSeat.getStatusSeat()
        );

        assertEquals(
                100,
                event.getEventAvailableSeats()
        );

        verify(bookingRepository, times(1))
                .save(booking);
    }

//    Booking te ber CANCEL
@Test
void cancelBooking_shouldThrowExceptionWhenBookingAlreadyCancelled() {

    booking.setBookingStatus(
            BookingStatus.CANCELLED
    );

    when(bookingRepository.findById(1L))
            .thenReturn(Optional.of(booking));

    when(authenticatedUserService.getCurrentUser())
            .thenReturn(user);

    InvalidOperationException exception =
            assertThrows(
                    InvalidOperationException.class,
                    () -> bookingService.cancelBooking(1L)
            );

    assertEquals(
            "Booking is already cancelled",
            exception.getMessage()
    );

    verify(bookingRepository, never())
            .save(any(Booking.class));
}
// Booking COMPLETED
@Test
void cancelBooking_shouldThrowExceptionWhenBookingIsCompleted() {

    booking.setBookingStatus(
            BookingStatus.COMPLETED
    );

    when(bookingRepository.findById(1L))
            .thenReturn(Optional.of(booking));

    when(authenticatedUserService.getCurrentUser())
            .thenReturn(user);

    InvalidOperationException exception =
            assertThrows(
                    InvalidOperationException.class,
                    () -> bookingService.cancelBooking(1L)
            );

    assertEquals(
            "Completed booking cannot be cancelled",
            exception.getMessage()
    );

    verify(bookingRepository, never())
            .save(any(Booking.class));
}

//Seat eshte SOLD
@Test
void cancelBooking_shouldThrowExceptionWhenSeatIsSold() {

    BookingSeat bookingSeat = BookingSeat.builder()
            .booking(booking)
            .eventSeat(eventSeat)
            .build();

    booking.setBookingSeats(
            new ArrayList<>(List.of(bookingSeat))
    );

    booking.setBookingStatus(
            BookingStatus.CONFIRMED
    );

    eventSeat.setStatusSeat(
            StatusSeat.SOLD
    );

    when(bookingRepository.findById(1L))
            .thenReturn(Optional.of(booking));

    when(authenticatedUserService.getCurrentUser())
            .thenReturn(user);

    InvalidOperationException exception =
            assertThrows(
                    InvalidOperationException.class,
                    () -> bookingService.cancelBooking(1L)
            );

    assertEquals(
            "Confirmed seats cannot be released",
            exception.getMessage()
    );

    verify(bookingRepository, never())
            .save(any(Booking.class));
}
//OWNESHIP: Nuk ndryshohet statusi CANCEL i booking te nje OWNER tjeter

    @Test
    void cancelBooking_shouldThrowExceptionWhenUserIsNotOwner() {

        User anotherUser = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("User")
                .email("other.user@test.com")
                .role(Role.USER)
                .build();

        booking.setBookingStatus(
                BookingStatus.PENDING
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherUser);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.cancelBooking(1L)
                );

        assertEquals(
                "You are not allowed to access or modify this booking",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

//    Booking CONFIRMED kalon me sukses ne COMPLETED

    @Test
    void completeBooking_shouldCompleteBookingSuccessfully() {

        booking.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(bookingRepository.save(booking))
                .thenReturn(booking);

        response.setBookingStatus(
                BookingStatus.COMPLETED
        );

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.completeBooking(1L);

        assertNotNull(result);

        assertEquals(
                BookingStatus.COMPLETED,
                booking.getBookingStatus()
        );

        verify(bookingRepository, times(1))
                .save(booking);
    }

    //Booking qe nuk eshte CONFIRMED refuzohet
    @Test
    void completeBooking_shouldThrowExceptionWhenBookingIsNotConfirmed() {

        booking.setBookingStatus(
                BookingStatus.PENDING
        );


        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.completeBooking(1L)
                );

        assertEquals(
                "Only confirmed bookings can be completed",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

//     ORGANIZER qe nuk eshte owner i Event refuzohet


    @Test
    void completeBooking_shouldThrowExceptionWhenOrganizerIsNotEventOwner() {

        User anotherOrganizer = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("Organizer")
                .email("other.organizer@test.com")
                .role(Role.ORGANIZER)
                .build();

        booking.setBookingStatus(
                BookingStatus.CONFIRMED
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherOrganizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.completeBooking(1L)
                );

        assertEquals(
                "You are not allowed to manage bookings for this event",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .save(any(Booking.class));
    }

//    Gjetja me ID E BOOKING

    @Test
    void getBookingById_shouldReturnBookingSuccessfully() {

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(bookingMapper.toResponse(booking))
                .thenReturn(response);

        BookingResponse result =
                bookingService.getBookingById(1L);

        assertNotNull(result);

        assertEquals(
                response,
                result
        );

        verify(bookingRepository, times(1))
                .findById(1L);

        verify(bookingMapper, times(1))
                .toResponse(booking);
    }

    //Gjetja sipas ID BOOKING vetem kur eshte OWNER
    @Test
    void getBookingById_shouldThrowExceptionWhenUserIsNotOwner() {

        User anotherUser = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("User")
                .email("other.user@test.com")
                .role(Role.USER)
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherUser);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.getBookingById(1L)
                );

        assertEquals(
                "You are not allowed to access or modify this booking",
                exception.getMessage()
        );

        verify(bookingMapper, never())
                .toResponse(any(Booking.class));
    }

//    USER sheh listen e booking te llogarise se tij

    @Test
    void getMyBookings_shouldReturnCurrentUserBookings() {

        List<Booking> bookings =
                List.of(booking);

        List<BookingResponse> responses =
                List.of(response);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(bookingRepository.findByUserId(user.getId()))
                .thenReturn(bookings);

        when(bookingMapper.toResponseList(bookings))
                .thenReturn(responses);

        List<BookingResponse> result =
                bookingService.getMyBookings();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1))
                .findByUserId(user.getId());
    }

//Filtri i BOOKING  sipas nje EVENTI

    @Test
    void getBookingsByEvent_shouldReturnBookingsSuccessfully() {

        List<Booking> bookings =
                List.of(booking);

        List<BookingResponse> responses =
                List.of(response);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(bookingRepository.findByEventId(1L))
                .thenReturn(bookings);

        when(bookingMapper.toResponseList(bookings))
                .thenReturn(responses);

        List<BookingResponse> result =
                bookingService.getBookingsByEvent(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1))
                .findByEventId(1L);
    }

//    ORGANIZER nuk eshte nje OWNER

    @Test
    void getBookingsByEvent_shouldThrowExceptionWhenOrganizerIsNotOwner() {

        User anotherOrganizer = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("Organizer")
                .email("other.organizer@test.com")
                .role(Role.ORGANIZER)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherOrganizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> bookingService.getBookingsByEvent(1L)
                );

        assertEquals(
                "You are not allowed to manage bookings for this event",
                exception.getMessage()
        );

        verify(bookingRepository, never())
                .findByEventId(anyLong());
    }

//    Filtri i BOOKING sipas Statusit

    @Test
    void getMyBookingsByStatus_shouldReturnBookingsWithSelectedStatus() {

        List<Booking> bookings =
                List.of(booking);

        List<BookingResponse> responses =
                List.of(response);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(bookingRepository
                .findByUserIdAndBookingStatus(
                        user.getId(),
                        BookingStatus.PENDING
                ))
                .thenReturn(bookings);

        when(bookingMapper.toResponseList(bookings))
                .thenReturn(responses);

        List<BookingResponse> result =
                bookingService.getMyBookingsByStatus(
                        BookingStatus.PENDING
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(bookingRepository, times(1))
                .findByUserIdAndBookingStatus(
                        user.getId(),
                        BookingStatus.PENDING
                );
    }
}