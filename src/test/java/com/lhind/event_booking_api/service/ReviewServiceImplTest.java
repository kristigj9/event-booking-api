package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.ReviewMapper;
import com.lhind.event_booking_api.repository.BookingRepository;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.ReviewRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User user;
    private Event event;
    private Review review;
    private ReviewRequest request;
    private ReviewResponse response;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.USER)
                .build();

        event = Event.builder()
                .id(1L)
                .eventName("Music Event")
                .eventStartDateTime(
                        LocalDateTime.now().minusHours(4)
                )
                .eventEndDateTime(
                        LocalDateTime.now().minusHours(1)
                )
                .eventStatus(EventStatus.COMPLETED)
                .build();

        request = new ReviewRequest();
        request.setRatingReview(5);
        request.setCommentReview("Great event");
        request.setEventId(1L);

        review = Review.builder()
                .id(1L)
                .ratingReview(5)
                .commentReview("Great event")
                .user(user)
                .event(event)
                .build();

        response = ReviewResponse.builder()
                .id(1L)
                .ratingReview(5)
                .commentReview("Great event")
                .userId(1L)
                .eventId(1L)
                .build();
    }

    @Test
    void createReview_shouldCreateReviewSuccessfully() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(bookingRepository
                .existsByUserIdAndEventIdAndBookingStatus(
                        1L,
                        1L,
                        BookingStatus.COMPLETED
                ))
                .thenReturn(true);

        when(reviewRepository
                .existsByUserIdAndEventId(
                        1L,
                        1L
                ))
                .thenReturn(false);

        when(reviewMapper.toEntity(
                request,
                user,
                event
        )).thenReturn(review);

        when(reviewRepository.save(review))
                .thenReturn(review);

        when(reviewMapper.toResponse(review))
                .thenReturn(response);

        ReviewResponse result =
                reviewService.createReview(request);

        assertNotNull(result);

        assertEquals(
                5,
                result.getRatingReview()
        );

        verify(reviewRepository, times(1))
                .save(review);
    }

    @Test
    void createReview_shouldThrowExceptionWhenEventHasNotEnded() {

        event.setEventEndDateTime(
                LocalDateTime.now().plusHours(2)
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> reviewService.createReview(request)
                );

        assertEquals(
                "Review can only be created after the event has ended",
                exception.getMessage()
        );

        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    @Test
    void createReview_shouldThrowExceptionWhenUserHasNoCompletedBooking() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(bookingRepository
                .existsByUserIdAndEventIdAndBookingStatus(
                        1L,
                        1L,
                        BookingStatus.COMPLETED
                ))
                .thenReturn(false);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> reviewService.createReview(request)
                );

        assertEquals(
                "You can review only events you have attended",
                exception.getMessage()
        );

        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    @Test
    void createReview_shouldThrowExceptionWhenReviewAlreadyExists() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(bookingRepository
                .existsByUserIdAndEventIdAndBookingStatus(
                        1L,
                        1L,
                        BookingStatus.COMPLETED
                ))
                .thenReturn(true);

        when(reviewRepository
                .existsByUserIdAndEventId(
                        1L,
                        1L
                ))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> reviewService.createReview(request)
                );

        assertEquals(
                "User has already reviewed this event",
                exception.getMessage()
        );

        verify(reviewRepository, never())
                .save(any(Review.class));
    }

//    getReviewById() - success

    @Test
    void getReviewById_shouldReturnReviewSuccessfully() {

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(reviewMapper.toResponse(review))
                .thenReturn(response);

        ReviewResponse result =
                reviewService.getReviewById(1L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(reviewRepository, times(1))
                .findById(1L);

        verify(reviewMapper, times(1))
                .toResponse(review);
    }

//    getReviewById() - review nuk ekziston

    @Test
    void getReviewById_shouldThrowExceptionWhenReviewNotFound() {

        when(reviewRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> reviewService.getReviewById(99L)
                );

        assertEquals(
                "Review not found with id: 99",
                exception.getMessage()
        );

        verify(reviewMapper, never())
                .toResponse(any(Review.class));
    }

//    getReviewsByEvent()

    @Test
    void getReviewsByEvent_shouldReturnReviewsSuccessfully() {

        List<Review> reviews =
                List.of(review);

        List<ReviewResponse> responses =
                List.of(response);

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(reviewRepository.findByEventId(1L))
                .thenReturn(reviews);

        when(reviewMapper.toResponseList(reviews))
                .thenReturn(responses);

        List<ReviewResponse> result =
                reviewService.getReviewsByEvent(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(eventRepository, times(1))
                .findById(1L);

        verify(reviewRepository, times(1))
                .findByEventId(1L);
    }

//     getReviewsByEvent() - event nuk ekziston

    @Test
    void getReviewsByEvent_shouldThrowExceptionWhenEventNotFound() {

        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> reviewService.getReviewsByEvent(99L)
                );

        assertEquals(
                "Event not found with id: 99",
                exception.getMessage()
        );

        verify(reviewRepository, never())
                .findByEventId(anyLong());
    }

//    getMyReviews()

    @Test
    void getMyReviews_shouldReturnCurrentUserReviews() {

        List<Review> reviews =
                List.of(review);

        List<ReviewResponse> responses =
                List.of(response);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(reviewRepository.findByUserId(1L))
                .thenReturn(reviews);

        when(reviewMapper.toResponseList(reviews))
                .thenReturn(responses);

        List<ReviewResponse> result =
                reviewService.getMyReviews();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(reviewRepository, times(1))
                .findByUserId(1L);
    }

//    UPDATE REVIEW
@Test
void updateReview_shouldUpdateReviewSuccessfully() {

    ReviewRequest updateRequest =
            new ReviewRequest();

    updateRequest.setRatingReview(4);
    updateRequest.setCommentReview("Very good event");
    updateRequest.setEventId(1L);

    when(reviewRepository.findById(1L))
            .thenReturn(Optional.of(review));

    when(authenticatedUserService.getCurrentUser())
            .thenReturn(user);

    when(reviewRepository.save(review))
            .thenReturn(review);

    when(reviewMapper.toResponse(review))
            .thenReturn(response);

    ReviewResponse result =
            reviewService.updateReview(
                    1L,
                    updateRequest
            );

    assertNotNull(result);

    verify(reviewMapper, times(1))
            .updateReview(
                    updateRequest,
                    review
            );

    verify(reviewRepository, times(1))
            .save(review);
}

    @Test
    void updateReview_shouldThrowExceptionWhenUserIsNotOwner() {

        User anotherUser = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("User")
                .email("other@test.com")
                .role(Role.USER)
                .build();

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherUser);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> reviewService.updateReview(
                                1L,
                                request
                        )
                );

        assertEquals(
                "You are not allowed to modify this review",
                exception.getMessage()
        );

        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    //Testi qe review nuk mund te kaloje ne event tjeter
    @Test
    void updateReview_shouldThrowExceptionWhenChangingEvent() {

        ReviewRequest updateRequest =
                new ReviewRequest();

        updateRequest.setRatingReview(4);
        updateRequest.setCommentReview("Updated review");

        // Review aktual eshte per event 1
        updateRequest.setEventId(2L);

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> reviewService.updateReview(
                                1L,
                                updateRequest
                        )
                );

        assertEquals(
                "Review cannot be moved to another event",
                exception.getMessage()
        );

        verify(reviewMapper, never())
                .updateReview(
                        any(ReviewRequest.class),
                        any(Review.class)
                );

        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    // deleteReview() success

    @Test
    void deleteReview_shouldDeleteReviewSuccessfully() {

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1))
                .delete(review);
    }
    //Delete nga user jo-owner
    @Test
    void deleteReview_shouldThrowExceptionWhenUserIsNotOwner() {

        User anotherUser = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("User")
                .email("other@test.com")
                .role(Role.USER)
                .build();

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherUser);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> reviewService.deleteReview(1L)
                );

        assertEquals(
                "You are not allowed to modify this review",
                exception.getMessage()
        );

        verify(reviewRepository, never())
                .delete(any(Review.class));
    }

    //ADMIN BEN DELETE dhe pse nuk eshte OWNER

    @Test
    void deleteReview_shouldAllowAdminToDeleteAnotherUsersReview() {

        User admin = User.builder()
                .id(50L)
                .firstName("Admin")
                .lastName("User")
                .email("admin@test.com")
                .role(Role.ADMIN)
                .build();

        when(reviewRepository.findById(1L))
                .thenReturn(Optional.of(review));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(admin);

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1))
                .delete(review);
    }


}