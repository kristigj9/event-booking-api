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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    private User user;
    private Event event;
    private Review review;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("review.user@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        user = userRepository.save(user);

        User organizer = User.builder()
                .firstName("Test")
                .lastName("Organizer")
                .email("review.organizer@test.com")
                .password("encoded-password")
                .role(Role.ORGANIZER)
                .build();

        organizer =
                userRepository.save(organizer);

        Venue venue = Venue.builder()
                .venueName("Review Arena")
                .venueAddress("Tirana")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        venue =
                venueRepository.save(venue);

        event = Event.builder()
                .eventName("Review Test Event")
                .eventDescription("Event for review tests")
                .eventStartDateTime(
                        LocalDateTime.now()
                                .minusHours(4)
                )
                .eventEndDateTime(
                        LocalDateTime.now()
                                .minusHours(1)
                )
                .eventTotalSeats(100)
                .eventAvailableSeats(90)
                .eventStatus(
                        EventStatus.COMPLETED
                )
                .organizer(organizer)
                .venue(venue)
                .categories(
                        new ArrayList<>()
                )
                .build();

        event =
                eventRepository.save(event);

        review = Review.builder()
                .ratingReview(5)
                .commentReview(
                        "Great event"
                )
                .user(user)
                .event(event)
                .build();

        review =
                reviewRepository.save(review);
    }

    @Test
    void findByEventId_shouldReturnEventReviews() {

        List<Review> result =
                reviewRepository.findByEventId(
                        event.getId()
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                event.getId(),
                result.get(0)
                        .getEvent()
                        .getId()
        );
    }

    @Test
    void findByUserId_shouldReturnUserReviews() {

        List<Review> result =
                reviewRepository.findByUserId(
                        user.getId()
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                user.getId(),
                result.get(0)
                        .getUser()
                        .getId()
        );
    }

    @Test
    void findByUserIdAndEventId_shouldReturnReview() {

        Optional<Review> result =
                reviewRepository
                        .findByUserIdAndEventId(
                                user.getId(),
                                event.getId()
                        );

        assertTrue(
                result.isPresent()
        );

        assertEquals(
                "Great event",
                result.get()
                        .getCommentReview()
        );

        assertEquals(
                5,
                result.get()
                        .getRatingReview()
        );
    }

    @Test
    void findByUserIdAndEventId_shouldReturnEmptyWhenReviewDoesNotExist() {

        Optional<Review> result =
                reviewRepository
                        .findByUserIdAndEventId(
                                999L,
                                event.getId()
                        );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void existsByUserIdAndEventId_shouldReturnTrue() {

        boolean result =
                reviewRepository
                        .existsByUserIdAndEventId(
                                user.getId(),
                                event.getId()
                        );

        assertTrue(result);
    }

    @Test
    void existsByUserIdAndEventId_shouldReturnFalseWhenReviewDoesNotExist() {

        boolean result =
                reviewRepository
                        .existsByUserIdAndEventId(
                                999L,
                                event.getId()
                        );

        assertFalse(result);
    }
}