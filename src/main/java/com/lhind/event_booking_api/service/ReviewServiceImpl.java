package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.Review;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.ReviewMapper;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.ReviewRepository;
import com.lhind.event_booking_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            ReviewMapper reviewMapper
    ) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.reviewMapper = reviewMapper;
    }

    // CREATE
    @Override
    @Transactional
    public ReviewResponse createReview(
            Long userId,
            ReviewRequest request
    ) {

        User user = findUser(userId);

        Event event = findEvent(request.getEventId());

        if (reviewRepository.existsByUserIdAndEventId(
                userId,
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
    public ReviewResponse getReviewById(Long reviewId) {

        Review review = findReview(reviewId);

        return reviewMapper.toResponse(review);
    }

    // GET REVIEWS BY EVENT
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByEvent(Long eventId) {

        findEvent(eventId);

        return reviewMapper.toResponseList(
                reviewRepository.findByEventId(eventId)
        );
    }

    // GET REVIEWS BY USER
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUser(Long userId) {

        findUser(userId);

        return reviewMapper.toResponseList(
                reviewRepository.findByUserId(userId)
        );
    }

    // UPDATE
    @Override
    @Transactional
    public ReviewResponse updateReview(
            Long reviewId,
            Long userId,
            ReviewRequest request
    ) {

        Review review = findReview(reviewId);

        validateOwnership(review, userId);

        /*
         * Nuk lejojm te behet update i nje Review
         * ta transferojë ate te nje Event tjeter.
         */
        if (!review.getEvent()
                .getId()
                .equals(request.getEventId())) {

            throw new InvalidOperationException(
                    "Review cannot be moved to another event"
            );
        }

        reviewMapper.updateReview(request, review);

        Review updatedReview =
                reviewRepository.save(review);

        return reviewMapper.toResponse(updatedReview);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteReview(
            Long reviewId,
            Long userId
    ) {

        Review review = findReview(reviewId);

        validateOwnership(review, userId);

        reviewRepository.delete(review);
    }


    // PRIVATE HELPER METHODS

    private Review findReview(Long reviewId) {

        return reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Review not found with id: "
                                        + reviewId
                        )
                );
    }

    private User findUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );
    }

    private Event findEvent(Long eventId) {

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
            Long userId
    ) {

        if (!review.getUser()
                .getId()
                .equals(userId)) {

            throw new InvalidOperationException(
                    "You are not allowed to modify this review"
            );
        }
    }
}