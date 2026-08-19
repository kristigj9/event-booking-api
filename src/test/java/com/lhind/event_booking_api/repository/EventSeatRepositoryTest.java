package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class EventSeatRepositoryTest {

    @Autowired
    private EventSeatRepository eventSeatRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private UserRepository userRepository;

    private Event event;
    private Seat seat1;
    private Seat seat2;
    private EventSeat eventSeat1;
    private EventSeat eventSeat2;

    @BeforeEach
    void setUp() {

        User organizer = User.builder()
                .firstName("Test")
                .lastName("Organizer")
                .email("eventseat.organizer@test.com")
                .password("encoded-password")
                .role(Role.ORGANIZER)
                .build();

        organizer =
                userRepository.save(organizer);

        Venue venue = Venue.builder()
                .venueName("Event Seat Arena")
                .venueAddress("Tirana")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        venue =
                venueRepository.save(venue);

        seat1 = Seat.builder()
                .rowNumber("A")
                .seatNumber(1)
                .venue(venue)
                .build();

        seat2 = Seat.builder()
                .rowNumber("A")
                .seatNumber(2)
                .venue(venue)
                .build();

        seat1 = seatRepository.save(seat1);
        seat2 = seatRepository.save(seat2);

        event = Event.builder()
                .eventName("Event Seat Test")
                .eventDescription("Repository test")
                .eventStartDateTime(
                        LocalDateTime.now().plusDays(5)
                )
                .eventEndDateTime(
                        LocalDateTime.now()
                                .plusDays(5)
                                .plusHours(3)
                )
                .eventTotalSeats(100)
                .eventAvailableSeats(100)
                .eventStatus(EventStatus.PUBLISHED)
                .organizer(organizer)
                .venue(venue)
                .categories(new ArrayList<>())
                .build();

        event =
                eventRepository.save(event);

        eventSeat1 = EventSeat.builder()
                .event(event)
                .seat(seat1)
                .statusSeat(StatusSeat.AVAILABLE)
                .priceSeat(new BigDecimal("25.00"))
                .build();

        eventSeat2 = EventSeat.builder()
                .event(event)
                .seat(seat2)
                .statusSeat(StatusSeat.RESERVED)
                .priceSeat(new BigDecimal("30.00"))
                .build();

        eventSeat1 =
                eventSeatRepository.save(eventSeat1);

        eventSeat2 =
                eventSeatRepository.save(eventSeat2);
    }

    @Test
    void findByEventId_shouldReturnEventSeats() {

        List<EventSeat> result =
                eventSeatRepository.findByEventId(
                        event.getId()
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(eventSeat ->
                                eventSeat.getEvent()
                                        .getId()
                                        .equals(event.getId())
                        )
        );
    }

    @Test
    void findByEventIdAndStatusSeat_shouldReturnAvailableSeats() {

        List<EventSeat> result =
                eventSeatRepository
                        .findByEventIdAndStatusSeat(
                                event.getId(),
                                StatusSeat.AVAILABLE
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                StatusSeat.AVAILABLE,
                result.get(0)
                        .getStatusSeat()
        );

        assertEquals(
                seat1.getId(),
                result.get(0)
                        .getSeat()
                        .getId()
        );
    }

    @Test
    void findByEventIdAndStatusSeat_shouldReturnReservedSeats() {

        List<EventSeat> result =
                eventSeatRepository
                        .findByEventIdAndStatusSeat(
                                event.getId(),
                                StatusSeat.RESERVED
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                StatusSeat.RESERVED,
                result.get(0)
                        .getStatusSeat()
        );
    }

    @Test
    void findByEventIdAndSeatId_shouldReturnEventSeat() {

        Optional<EventSeat> result =
                eventSeatRepository
                        .findByEventIdAndSeatId(
                                event.getId(),
                                seat1.getId()
                        );

        assertTrue(result.isPresent());

        assertEquals(
                eventSeat1.getId(),
                result.get().getId()
        );

        assertEquals(
                seat1.getId(),
                result.get()
                        .getSeat()
                        .getId()
        );
    }

    @Test
    void findByEventIdAndSeatId_shouldReturnEmptyWhenNotFound() {

        Optional<EventSeat> result =
                eventSeatRepository
                        .findByEventIdAndSeatId(
                                event.getId(),
                                999L
                        );

        assertTrue(result.isEmpty());
    }
}