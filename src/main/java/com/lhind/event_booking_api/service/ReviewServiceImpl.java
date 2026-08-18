package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.Review;
import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.ReviewMapper;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.ReviewRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;
    private final ReviewMapper reviewMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            EventRepository eventRepository,
            ReviewMapper reviewMapper,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
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

        Event event =
                findEvent(request.getEventId());

        if (reviewRepository.existsByUserIdAndEventId(
                user.getId(),
                event.getId()
        )) {

            throw new DuplicateResourceException(
                    "User has already reviewed this event"
            );
        }

        Review review = reviewMapper.toEntity(
                request,
                user,
                event
        );

        Review savedReview =
                reviewRepository.save(review);

        return reviewMapper.toResponse(savedReview);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(
            Long reviewId
    ) {

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

        validateOwnership(
                review,
                currentUser
        );

        /*
         * Nuk lejojmë që një Review të transferohet
         * nga një Event te një Event tjetër.
         */
        if (!review.getEvent()
                .getId()
                .equals(request.getEventId())) {

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

        validateOwnership(
                review,
                currentUser
        );

        reviewRepository.delete(review);
    }

    // ----------------------------
    // PRIVATE HELPER METHODS
    // ----------------------------

    private Review findReview(
            Long reviewId
    ) {

        return reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found with id: "
                                        + reviewId
                        )
                );
    }

    private Event findEvent(
            Long eventId
    ) {

        return eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: "
                                        + eventId
                        )
                );
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

            throw new InvalidOperationException(
                    "You are not allowed to modify this review"
            );
        }
    }
}