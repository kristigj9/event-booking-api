package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;
import com.lhind.event_booking_api.entity.BookingStatus;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.Review;
import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.ReviewMapper;
import com.lhind.event_booking_api.repository.BookingRepository;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.ReviewRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log =
            LogManager.getLogger(ReviewServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final ReviewMapper reviewMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            EventRepository eventRepository,
            BookingRepository bookingRepository,
            ReviewMapper reviewMapper,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.reviewMapper = reviewMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    // CREATE
    @Override
    @Transactional
    public ReviewResponse createReview(
            ReviewRequest request
    ) {

        User user =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Creating review for user id: {} and event id: {}",
                user.getId(),
                request.getEventId()
        );

        Event event =
                findEvent(request.getEventId());

        if (event.getEventEndDateTime()
                .isAfter(LocalDateTime.now())) {

            log.warn(
                    "Review creation rejected. Event id: {} has not ended yet",
                    event.getId()
            );

            throw new InvalidOperationException(
                    "Review can only be created after the event has ended"
            );
        }

        boolean hasCompletedBooking =
                bookingRepository
                        .existsByUserIdAndEventIdAndBookingStatus(
                                user.getId(),
                                event.getId(),
                                BookingStatus.COMPLETED
                        );

        if (!hasCompletedBooking) {

            log.warn(
                    "Review creation rejected. User id: {} has no completed booking for event id: {}",
                    user.getId(),
                    event.getId()
            );

            throw new InvalidOperationException(
                    "You can review only events you have attended"
            );
        }

        if (reviewRepository.existsByUserIdAndEventId(
                user.getId(),
                event.getId()
        )) {

            log.warn(
                    "Duplicate review attempt. User id: {}, event id: {}",
                    user.getId(),
                    event.getId()
            );

            throw new DuplicateResourceException(
                    "User has already reviewed this event"
            );
        }

        Review review =
                reviewMapper.toEntity(
                        request,
                        user,
                        event
                );

        Review savedReview =
                reviewRepository.save(review);

        log.info(
                "Review created successfully with id: {}",
                savedReview.getId()
        );

        return reviewMapper.toResponse(savedReview);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(
            Long reviewId
    ) {

        log.debug(
                "Fetching review by id: {}",
                reviewId
        );

        Review review =
                findReview(reviewId);

        return reviewMapper.toResponse(review);
    }

    // GET REVIEWS BY EVENT
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByEvent(
            Long eventId
    ) {

        log.debug(
                "Fetching reviews for event id: {}",
                eventId
        );

        findEvent(eventId);

        return reviewMapper.toResponseList(
                reviewRepository.findByEventId(eventId)
        );
    }

    // GET REVIEWS OF CURRENT USER
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviews() {

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.debug(
                "Fetching reviews for user id: {}",
                currentUser.getId()
        );

        return reviewMapper.toResponseList(
                reviewRepository.findByUserId(
                        currentUser.getId()
                )
        );
    }

    // UPDATE
    @Override
    @Transactional
    public ReviewResponse updateReview(
            Long reviewId,
            ReviewRequest request
    ) {

        Review review =
                findReview(reviewId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Update requested for review id: {} by user id: {}",
                reviewId,
                currentUser.getId()
        );

        validateOwnership(
                review,
                currentUser
        );

        if (!review.getEvent()
                .getId()
                .equals(request.getEventId())) {

            log.warn(
                    "Review id: {} cannot be moved from event id: {} to event id: {}",
                    reviewId,
                    review.getEvent().getId(),
                    request.getEventId()
            );

            throw new InvalidOperationException(
                    "Review cannot be moved to another event"
            );
        }

        reviewMapper.updateReview(
                request,
                review
        );

        Review updatedReview =
                reviewRepository.save(review);

        log.info(
                "Review updated successfully with id: {}",
                updatedReview.getId()
        );

        return reviewMapper.toResponse(updatedReview);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteReview(
            Long reviewId
    ) {

        Review review =
                findReview(reviewId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Delete requested for review id: {} by user id: {}",
                reviewId,
                currentUser.getId()
        );

        validateOwnership(
                review,
                currentUser
        );

        reviewRepository.delete(review);

        log.info(
                "Review deleted successfully with id: {}",
                reviewId
        );
    }

    // ----------------------------
    // PRIVATE HELPER METHODS
    // ----------------------------

    private Review findReview(
            Long reviewId
    ) {

        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> {

                    log.warn(
                            "Review not found with id: {}",
                            reviewId
                    );

                    return new ResourceNotFoundException(
                            "Review not found with id: "
                                    + reviewId
                    );
                });
    }

    private Event findEvent(
            Long eventId
    ) {

        return eventRepository.findById(eventId)
                .orElseThrow(() -> {

                    log.warn(
                            "Event not found with id: {}",
                            eventId
                    );

                    return new ResourceNotFoundException(
                            "Event not found with id: "
                                    + eventId
                    );
                });
    }

    private void validateOwnership(
            Review review,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        boolean isOwner =
                review.getUser()
                        .getId()
                        .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {

            log.warn(
                    "Unauthorized review modification attempt. Review id: {}, user id: {}",
                    review.getId(),
                    currentUser.getId()
            );

            throw new InvalidOperationException(
                    "You are not allowed to modify this review"
            );
        }

        log.debug(
                "Review ownership validation successful. Review id: {}, user id: {}, admin: {}",
                review.getId(),
                currentUser.getId(),
                isAdmin
        );
    }
}