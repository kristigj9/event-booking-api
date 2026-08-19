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
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User organizer;
    private Venue venue;
    private Category musicCategory;
    private Category sportCategory;

    @BeforeEach
    void setUp() {

        organizer = User.builder()
                .firstName("Test")
                .lastName("Organizer")
                .email("event.organizer@test.com")
                .password("encoded-password")
                .role(Role.ORGANIZER)
                .build();

        organizer =
                userRepository.save(organizer);

        venue = Venue.builder()
                .venueName("Tirana Arena")
                .venueAddress("Tirana")
                .venueCity("Tirana")
                .venueCapacity(500)
                .build();

        venue =
                venueRepository.save(venue);

        musicCategory = Category.builder()
                .nameCategory("Music")
                .descriptionCategory("Music events")
                .build();

        musicCategory =
                categoryRepository.save(musicCategory);

        sportCategory = Category.builder()
                .nameCategory("Sport")
                .descriptionCategory("Sport events")
                .build();

        sportCategory =
                categoryRepository.save(sportCategory);

        Event publishedMusicEvent =
                Event.builder()
                        .eventName("Music Festival")
                        .eventDescription("Music event")
                        .eventStartDateTime(
                                LocalDateTime.now()
                                        .plusDays(5)
                        )
                        .eventEndDateTime(
                                LocalDateTime.now()
                                        .plusDays(5)
                                        .plusHours(3)
                        )
                        .eventTotalSeats(100)
                        .eventAvailableSeats(100)
                        .eventStatus(
                                EventStatus.PUBLISHED
                        )
                        .organizer(organizer)
                        .venue(venue)
                        .categories(
                                new ArrayList<>(
                                        List.of(musicCategory)
                                )
                        )
                        .build();

        Event draftSportEvent =
                Event.builder()
                        .eventName("Sport Event")
                        .eventDescription("Sport event")
                        .eventStartDateTime(
                                LocalDateTime.now()
                                        .plusDays(10)
                        )
                        .eventEndDateTime(
                                LocalDateTime.now()
                                        .plusDays(10)
                                        .plusHours(2)
                        )
                        .eventTotalSeats(200)
                        .eventAvailableSeats(200)
                        .eventStatus(
                                EventStatus.DRAFT
                        )
                        .organizer(organizer)
                        .venue(venue)
                        .categories(
                                new ArrayList<>(
                                        List.of(sportCategory)
                                )
                        )
                        .build();

        eventRepository.save(publishedMusicEvent);
        eventRepository.save(draftSportEvent);
    }

    @Test
    void findByOrganizerId_shouldReturnOrganizerEvents() {

        List<Event> result =
                eventRepository.findByOrganizerId(
                        organizer.getId()
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(event ->
                                event.getOrganizer()
                                        .getId()
                                        .equals(
                                                organizer.getId()
                                        )
                        )
        );
    }

    @Test
    void findByEventStatus_shouldReturnPublishedEvents() {

        List<Event> result =
                eventRepository.findByEventStatus(
                        EventStatus.PUBLISHED
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "Music Festival",
                result.get(0).getEventName()
        );

        assertEquals(
                EventStatus.PUBLISHED,
                result.get(0).getEventStatus()
        );
    }

    @Test
    void findByCategoriesId_shouldReturnEventsByCategory() {

        List<Event> result =
                eventRepository.findByCategoriesId(
                        musicCategory.getId()
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "Music Festival",
                result.get(0).getEventName()
        );
    }

    @Test
    void findByVenueId_shouldReturnEventsByVenue() {

        List<Event> result =
                eventRepository.findByVenueId(
                        venue.getId()
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(event ->
                                event.getVenue()
                                        .getId()
                                        .equals(
                                                venue.getId()
                                        )
                        )
        );
    }

    @Test
    void findByOrganizerIdAndEventStatus_shouldReturnMatchingEvents() {

        List<Event> result =
                eventRepository
                        .findByOrganizerIdAndEventStatus(
                                organizer.getId(),
                                EventStatus.PUBLISHED
                        );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "Music Festival",
                result.get(0).getEventName()
        );

        assertEquals(
                EventStatus.PUBLISHED,
                result.get(0).getEventStatus()
        );
    }
}