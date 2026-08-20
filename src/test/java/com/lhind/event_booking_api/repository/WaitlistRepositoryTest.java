package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class WaitlistRepositoryTest {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;


    @Test
    void existsByUserIdAndEventId_shouldReturnTrueWhenWaitlistExists() {

        User user = createAndSaveUser(
                "waitlist1@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Waitlist Venue 1"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Waitlist Event 1"
        );

        Waitlist waitlist = Waitlist.builder()
                .requestedSeats(2)
                .status(WaitlistStatus.WAITING)
                .user(user)
                .event(event)
                .build();

        waitlistRepository.save(waitlist);

        boolean exists =
                waitlistRepository
                        .existsByUserIdAndEventId(
                                user.getId(),
                                event.getId()
                        );

        assertTrue(exists);
    }


    @Test
    void existsByUserIdAndEventId_shouldReturnFalseWhenWaitlistDoesNotExist() {

        User user = createAndSaveUser(
                "waitlist2@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Waitlist Venue 2"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Waitlist Event 2"
        );

        boolean exists =
                waitlistRepository
                        .existsByUserIdAndEventId(
                                user.getId(),
                                event.getId()
                        );

        assertFalse(exists);
    }


    @Test
    void findByUserIdAndEventId_shouldReturnWaitlist() {

        User user = createAndSaveUser(
                "waitlist3@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Waitlist Venue 3"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Waitlist Event 3"
        );

        Waitlist waitlist = Waitlist.builder()
                .requestedSeats(3)
                .status(WaitlistStatus.WAITING)
                .user(user)
                .event(event)
                .build();

        waitlistRepository.save(waitlist);

        Optional<Waitlist> result =
                waitlistRepository
                        .findByUserIdAndEventId(
                                user.getId(),
                                event.getId()
                        );

        assertTrue(result.isPresent());

        assertEquals(
                3,
                result.get().getRequestedSeats()
        );

        assertEquals(
                WaitlistStatus.WAITING,
                result.get().getStatus()
        );
    }


    @Test
    void findByEventIdOrderByCreatedAtAsc_shouldReturnWaitlistsInFIFOOrder() {

        User organizer = createAndSaveUser(
                "organizer@test.com"
        );

        User user1 = createAndSaveUser(
                "waitlist4@test.com"
        );

        User user2 = createAndSaveUser(
                "waitlist5@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Waitlist Venue 4"
        );

        Event event = createAndSaveEvent(
                organizer,
                venue,
                "Waitlist Event 4"
        );

        Waitlist first = Waitlist.builder()
                .requestedSeats(1)
                .createdAt(
                        LocalDateTime.now()
                                .minusMinutes(10)
                )
                .status(WaitlistStatus.WAITING)
                .user(user1)
                .event(event)
                .build();

        Waitlist second = Waitlist.builder()
                .requestedSeats(2)
                .createdAt(
                        LocalDateTime.now()
                                .minusMinutes(5)
                )
                .status(WaitlistStatus.WAITING)
                .user(user2)
                .event(event)
                .build();

        waitlistRepository.save(first);
        waitlistRepository.save(second);

        List<Waitlist> result =
                waitlistRepository
                        .findByEventIdOrderByCreatedAtAsc(
                                event.getId()
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                user1.getId(),
                result.get(0)
                        .getUser()
                        .getId()
        );

        assertEquals(
                user2.getId(),
                result.get(1)
                        .getUser()
                        .getId()
        );
    }


    @Test
    void findByUserIdOrderByCreatedAtDesc_shouldReturnUserWaitlists() {

        User user = createAndSaveUser(
                "waitlist6@test.com"
        );

        Venue venue1 = createAndSaveVenue(
                "Waitlist Venue 5"
        );

        Venue venue2 = createAndSaveVenue(
                "Waitlist Venue 6"
        );

        Event event1 = createAndSaveEvent(
                user,
                venue1,
                "Waitlist Event 5"
        );

        Event event2 = createAndSaveEvent(
                user,
                venue2,
                "Waitlist Event 6"
        );

        Waitlist first = Waitlist.builder()
                .requestedSeats(1)
                .createdAt(
                        LocalDateTime.now()
                                .minusMinutes(10)
                )
                .status(WaitlistStatus.WAITING)
                .user(user)
                .event(event1)
                .build();

        Waitlist second = Waitlist.builder()
                .requestedSeats(2)
                .createdAt(
                        LocalDateTime.now()
                )
                .status(WaitlistStatus.NOTIFIED)
                .user(user)
                .event(event2)
                .build();

        waitlistRepository.save(first);
        waitlistRepository.save(second);

        List<Waitlist> result =
                waitlistRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                user.getId()
                        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                event2.getId(),
                result.get(0)
                        .getEvent()
                        .getId()
        );

        assertEquals(
                event1.getId(),
                result.get(1)
                        .getEvent()
                        .getId()
        );
    }


    @Test
    void findByEventIdAndStatusOrderByCreatedAtAsc_shouldReturnOnlyMatchingStatus() {

        User organizer = createAndSaveUser(
                "organizer2@test.com"
        );

        User user1 = createAndSaveUser(
                "waitlist7@test.com"
        );

        User user2 = createAndSaveUser(
                "waitlist8@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Waitlist Venue 7"
        );

        Event event = createAndSaveEvent(
                organizer,
                venue,
                "Waitlist Event 7"
        );

        Waitlist waiting = Waitlist.builder()
                .requestedSeats(1)
                .status(WaitlistStatus.WAITING)
                .user(user1)
                .event(event)
                .build();

        Waitlist notified = Waitlist.builder()
                .requestedSeats(1)
                .status(WaitlistStatus.NOTIFIED)
                .user(user2)
                .event(event)
                .build();

        waitlistRepository.save(waiting);
        waitlistRepository.save(notified);

        List<Waitlist> result =
                waitlistRepository
                        .findByEventIdAndStatusOrderByCreatedAtAsc(
                                event.getId(),
                                WaitlistStatus.WAITING
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                WaitlistStatus.WAITING,
                result.get(0).getStatus()
        );

        assertEquals(
                user1.getId(),
                result.get(0)
                        .getUser()
                        .getId()
        );
    }


    // PRIVATE HELPER METHODS

    private User createAndSaveUser(
            String email
    ) {

        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }


    private Venue createAndSaveVenue(
            String venueName
    ) {

        Venue venue = Venue.builder()
                .venueName(venueName)
                .venueAddress("Test Address")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        return venueRepository.save(venue);
    }


    private Event createAndSaveEvent(
            User organizer,
            Venue venue,
            String eventName
    ) {

        Event event = Event.builder()
                .eventName(eventName)
                .eventDescription("Test Description")
                .eventStartDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                )
                .eventEndDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                                .plusHours(2)
                )
                .eventTotalSeats(100)
                .eventAvailableSeats(0)
                .eventStatus(
                        EventStatus.PUBLISHED
                )
                .venue(venue)
                .organizer(organizer)
                .build();

        return eventRepository.save(event);
    }
}