package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(
            Long userId,
            ReviewRequest request
    );

    ReviewResponse getReviewById(Long reviewId);

    List<ReviewResponse> getReviewsByEvent(Long eventId);

    List<ReviewResponse> getReviewsByUser(Long userId);

    ReviewResponse updateReview(
            Long reviewId,
            Long userId,
            ReviewRequest request
    );

    void deleteReview(
            Long reviewId,
            Long userId
    );
}