package com.lhind.event_booking_api.controller;
import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;
import com.lhind.event_booking_api.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService
    ) {
        this.reviewService = reviewService;
    }

    // USER / ORGANIZER / ADMIN
    // Krijon review per user-in e autentikuar
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request
    ) {

        ReviewResponse response =
                reviewService.createReview(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUBLIC
    // Merr nje review sipas ID
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(
            @PathVariable Long reviewId
    ) {

        return ResponseEntity.ok(
                reviewService.getReviewById(reviewId)
        );
    }

    // PUBLIC
    // Merr te gjitha reviews e nje eventi
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByEvent(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                reviewService.getReviewsByEvent(eventId)
        );
    }

    // AUTHENTICATED
    // Merr reviews e user-it aktual
    @GetMapping("/me")
    public ResponseEntity<List<ReviewResponse>> getMyReviews() {

        return ResponseEntity.ok(
                reviewService.getMyReviews()
        );
    }

    // OWNER / ADMIN
    // Pdrditson review
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request
    ) {

        return ResponseEntity.ok(
                reviewService.updateReview(
                        reviewId,
                        request
                )
        );
    }

    // OWNER / ADMIN
    // Fshin review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId
    ) {

        reviewService.deleteReview(reviewId);

        return ResponseEntity
                .noContent()
                .build();
    }
}

