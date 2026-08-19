package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.eventseat.EventSeatRequest;
import com.lhind.event_booking_api.dto.eventseat.EventSeatResponse;
import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.EventSeatMapper;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSeatServiceImplTest {

    @Mock
    private EventSeatRepository eventSeatRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private EventSeatMapper eventSeatMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private EventSeatServiceImpl eventSeatService;

    private User organizer;
    private Venue venue;
    private Event event;
    private Seat seat;
    private EventSeat eventSeat;
    private EventSeatRequest request;
    private EventSeatResponse response;

    @BeforeEach
    void setUp() {

        organizer = User.builder()
                .id(1L)
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
                .priceSeat(new BigDecimal("25.00"))
                .build();

        SeatSelectionRequest seatRequest =
                new SeatSelectionRequest();

        seatRequest.setRowNumber("A");
        seatRequest.setSeatNumber(1);

        request = new EventSeatRequest();
        request.setSeat(seatRequest);
        request.setPriceSeat(
                new BigDecimal("25.00")
        );

        response = EventSeatResponse.builder()
                .id(1L)
                .statusSeat(StatusSeat.AVAILABLE)
                .priceSeat(new BigDecimal("25.00"))
                .build();
    }

    @Test
    void createEventSeat_shouldCreateSuccessfully() {

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

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

        when(eventSeatRepository.save(any(EventSeat.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(eventSeatMapper.toResponse(any(EventSeat.class)))
                .thenReturn(response);

        EventSeatResponse result =
                eventSeatService.createEventSeat(
                        1L,
                        request
                );

        assertNotNull(result);

        assertEquals(
                StatusSeat.AVAILABLE,
                result.getStatusSeat()
        );

        assertEquals(
                new BigDecimal("25.00"),
                result.getPriceSeat()
        );

        verify(eventSeatRepository, times(1))
                .save(any(EventSeat.class));
    }

    @Test
    void createEventSeat_shouldThrowExceptionWhenOrganizerIsNotOwner() {

        User anotherOrganizer = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("Organizer")
                .email("other@test.com")
                .role(Role.ORGANIZER)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherOrganizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventSeatService.createEventSeat(
                                1L,
                                request
                        )
                );

        assertEquals(
                "You are not allowed to modify seats for this event",
                exception.getMessage()
        );

        verify(eventSeatRepository, never())
                .save(any(EventSeat.class));
    }

    @Test
    void createEventSeat_shouldThrowExceptionWhenSeatNotFound() {

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

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
                        () -> eventSeatService.createEventSeat(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Seat not found in event venue",
                exception.getMessage()
        );

        verify(eventSeatRepository, never())
                .save(any(EventSeat.class));
    }

    @Test
    void createEventSeat_shouldThrowExceptionWhenSeatAlreadyAssigned() {

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

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

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> eventSeatService.createEventSeat(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Seat is already assigned to this event",
                exception.getMessage()
        );

        verify(eventSeatRepository, never())
                .save(any(EventSeat.class));
    }

    @Test
    void getEventSeatById_shouldReturnEventSeatSuccessfully() {

        when(eventSeatRepository.findById(1L))
                .thenReturn(Optional.of(eventSeat));

        when(eventSeatMapper.toResponse(eventSeat))
                .thenReturn(response);

        EventSeatResponse result =
                eventSeatService.getEventSeatById(1L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(eventSeatRepository, times(1))
                .findById(1L);
    }

    @Test
    void getEventSeatById_shouldThrowExceptionWhenNotFound() {

        when(eventSeatRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> eventSeatService.getEventSeatById(99L)
                );

        assertEquals(
                "Event seat not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void getSeatsByEvent_shouldReturnSeatsSuccessfully() {

        List<EventSeat> eventSeats =
                List.of(eventSeat);

        List<EventSeatResponse> responses =
                List.of(response);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(eventSeatRepository.findByEventId(1L))
                .thenReturn(eventSeats);

        when(eventSeatMapper.toResponseList(eventSeats))
                .thenReturn(responses);

        List<EventSeatResponse> result =
                eventSeatService.getSeatsByEvent(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSeatsByEventAndStatus_shouldReturnSeatsSuccessfully() {

        List<EventSeat> eventSeats =
                List.of(eventSeat);

        List<EventSeatResponse> responses =
                List.of(response);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(eventSeatRepository
                .findByEventIdAndStatusSeat(
                        1L,
                        StatusSeat.AVAILABLE
                ))
                .thenReturn(eventSeats);

        when(eventSeatMapper.toResponseList(eventSeats))
                .thenReturn(responses);

        List<EventSeatResponse> result =
                eventSeatService.getSeatsByEventAndStatus(
                        1L,
                        StatusSeat.AVAILABLE
                );

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updatePrice_shouldUpdatePriceSuccessfully() {

        BigDecimal newPrice =
                new BigDecimal("35.00");

        when(eventSeatRepository.findById(1L))
                .thenReturn(Optional.of(eventSeat));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(eventSeatRepository.save(eventSeat))
                .thenReturn(eventSeat);

        response.setPriceSeat(newPrice);

        when(eventSeatMapper.toResponse(eventSeat))
                .thenReturn(response);

        EventSeatResponse result =
                eventSeatService.updatePrice(
                        1L,
                        newPrice
                );

        assertNotNull(result);

        assertEquals(
                newPrice,
                eventSeat.getPriceSeat()
        );

        verify(eventSeatRepository, times(1))
                .save(eventSeat);
    }
    @Test
    void updatePrice_shouldThrowExceptionWhenPriceIsInvalid() {

        BigDecimal invalidPrice =
                BigDecimal.ZERO;

        when(eventSeatRepository.findById(1L))
                .thenReturn(Optional.of(eventSeat));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventSeatService.updatePrice(
                                1L,
                                invalidPrice
                        )
                );

        assertEquals(
                "Seat price must be greater than 0",
                exception.getMessage()
        );

        verify(eventSeatRepository, never())
                .save(any(EventSeat.class));
    }

    @Test
    void updatePrice_shouldThrowExceptionWhenOrganizerIsNotOwner() {

        User anotherOrganizer = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("Organizer")
                .email("other@test.com")
                .role(Role.ORGANIZER)
                .build();

        when(eventSeatRepository.findById(1L))
                .thenReturn(Optional.of(eventSeat));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherOrganizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventSeatService.updatePrice(
                                1L,
                                new BigDecimal("30.00")
                        )
                );

        assertEquals(
                "You are not allowed to modify seats for this event",
                exception.getMessage()
        );

        verify(eventSeatRepository, never())
                .save(any(EventSeat.class));
    }

    @Test
    void deleteEventSeat_shouldDeleteSuccessfullyWhenAvailable() {

        eventSeat.setStatusSeat(
                StatusSeat.AVAILABLE
        );

        when(eventSeatRepository.findById(1L))
                .thenReturn(Optional.of(eventSeat));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        eventSeatService.deleteEventSeat(1L);

        verify(eventSeatRepository, times(1))
                .delete(eventSeat);
    }

    @Test
    void deleteEventSeat_shouldThrowExceptionWhenSeatIsReserved() {

        eventSeat.setStatusSeat(
                StatusSeat.RESERVED
        );

        when(eventSeatRepository.findById(1L))
                .thenReturn(Optional.of(eventSeat));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventSeatService.deleteEventSeat(1L)
                );

        assertEquals(
                "Only available event seats can be deleted",
                exception.getMessage()
        );

        verify(eventSeatRepository, never())
                .delete(any(EventSeat.class));
    }

    @Test
    void deleteEventSeat_shouldAllowAdminToDeleteAvailableSeat() {

        User admin = User.builder()
                .id(50L)
                .firstName("Admin")
                .lastName("User")
                .email("admin@test.com")
                .role(Role.ADMIN)
                .build();

        eventSeat.setStatusSeat(
                StatusSeat.AVAILABLE
        );

        when(eventSeatRepository.findById(1L))
                .thenReturn(Optional.of(eventSeat));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(admin);

        eventSeatService.deleteEventSeat(1L);

        verify(eventSeatRepository, times(1))
                .delete(eventSeat);
    }


}