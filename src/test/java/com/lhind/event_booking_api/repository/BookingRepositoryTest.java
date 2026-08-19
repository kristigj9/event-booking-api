package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    private User user;
    private User organizer;
    private Event event;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("booking.user@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        organizer = User.builder()
                .firstName("Test")
                .lastName("Organizer")
                .email("booking.organizer@test.com")
                .password("encoded-password")
                .role(Role.ORGANIZER)
                .build();

        organizer = userRepository.save(organizer);

        Venue venue = Venue.builder()
                .venueName("Booking Arena")
                .venueAddress("Tirana")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        venue = venueRepository.save(venue);

        event = Event.builder()
                .eventName("Booking Test Event")
                .eventDescription("Event for repository tests")
                .eventStartDateTime(
                        LocalDateTime.now().plusDays(5)
                )
                .eventEndDateTime(
                        LocalDateTime.now()
                                .plusDays(5)
                                .plusHours(3)
                )
                .eventTotalSeats(100)
                .eventAvailableSeats(98)
                .eventStatus(EventStatus.PUBLISHED)
                .organizer(organizer)
                .venue(venue)
                .categories(new ArrayList<>())
                .build();

        event = eventRepository.save(event);

        Booking pendingBooking =
                Booking.builder()
                        .user(user)
                        .event(event)
                        .bookingDate(
                                LocalDateTime.now()
                        )
                        .seatsBooked(1)
                        .bookingStatus(
                                BookingStatus.PENDING
                        )
                        .bookingSeats(
                                new ArrayList<>()
                        )
                        .build();

        Booking confirmedBooking =
                Booking.builder()
                        .user(user)
                        .event(event)
                        .bookingDate(
                                LocalDateTime.now()
                        )
                        .seatsBooked(1)
                        .bookingStatus(
                                BookingStatus.CONFIRMED
                        )
                        .bookingSeats(
                                new ArrayList<>()
                        )
                        .build();

        bookingRepository.save(pendingBooking);
        bookingRepository.save(confirmedBooking);
    }

    @Test
    void findByUserId_shouldReturnUserBookings() {

        List<Booking> result =
                bookingRepository.findByUserId(
                        user.getId()
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(booking ->
                                booking.getUser()
                                        .getId()
                                        .equals(user.getId())
                        )
        );
    }

    @Test
    void findByEventId_shouldReturnEventBookings() {

        List<Booking> result =
                bookingRepository.findByEventId(
                        event.getId()
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(booking ->
                                booking.getEvent()
                                        .getId()
                                        .equals(event.getId())
                        )
        );
    }

    @Test
    void findByUserIdAndBookingStatus_shouldReturnMatchingBookings() {

        List<Booking> result =
                bookingRepository
                        .findByUserIdAndBookingStatus(
                                user.getId(),
                                BookingStatus.PENDING
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                BookingStatus.PENDING,
                result.get(0)
                        .getBookingStatus()
        );
    }

    @Test
    void findByEventIdAndBookingStatus_shouldReturnMatchingBookings() {

        List<Booking> result =
                bookingRepository
                        .findByEventIdAndBookingStatus(
                                event.getId(),
                                BookingStatus.CONFIRMED
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                BookingStatus.CONFIRMED,
                result.get(0)
                        .getBookingStatus()
        );
    }

    @Test
    void existsByUserIdAndEventId_shouldReturnTrue() {

        boolean result =
                bookingRepository
                        .existsByUserIdAndEventId(
                                user.getId(),
                                event.getId()
                        );

        assertTrue(result);
    }

    @Test
    void existsByUserIdAndEventIdAndBookingStatus_shouldReturnTrue() {

        boolean result =
                bookingRepository
                        .existsByUserIdAndEventIdAndBookingStatus(
                                user.getId(),
                                event.getId(),
                                BookingStatus.CONFIRMED
                        );

        assertTrue(result);
    }

    @Test
    void existsByUserIdAndEventIdAndBookingStatus_shouldReturnFalseForWrongStatus() {

        boolean result =
                bookingRepository
                        .existsByUserIdAndEventIdAndBookingStatus(
                                user.getId(),
                                event.getId(),
                                BookingStatus.COMPLETED
                        );

        assertFalse(result);
    }
}