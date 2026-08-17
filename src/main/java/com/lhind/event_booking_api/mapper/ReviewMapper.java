package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.Review;
import com.lhind.event_booking_api.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ReviewMapper {

    // ReviewRequest + User + Event -> Review
    public Review toEntity(
            ReviewRequest request,
            User user,
            Event event
    ) {

        if (request == null) {
            return null;
        }

        return Review.builder()
                .ratingReview(request.getRatingReview())
                .commentReview(request.getCommentReview())
                .user(user)
                .event(event)
                .build();
    }

    // Review -> ReviewResponse
    public ReviewResponse toResponse(Review review) {

        if (review == null) {
            return null;
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .ratingReview(review.getRatingReview())
                .commentReview(review.getCommentReview())
                .dateTimeReview(review.getDateTimeReview())
                .userId(
                        review.getUser() != null
                                ? review.getUser().getId()
                                : null
                )
                .eventId(
                        review.getEvent() != null
                                ? review.getEvent().getId()
                                : null
                )
                .build();
    }

    // List<Review> -> List<ReviewResponse>
    public List<ReviewResponse> toResponseList(List<Review> reviews) {

        if (reviews == null) {
            return Collections.emptyList();
        }

        return reviews.stream()
                .map(this::toResponse)
                .toList();
    }

    // ReviewRequest + Review ekzistues -> update
    public void updateReview(
            ReviewRequest request,
            Review review
    ) {

        if (request == null || review == null) {
            return;
        }

        review.setRatingReview(request.getRatingReview());
        review.setCommentReview(request.getCommentReview());
    }
}