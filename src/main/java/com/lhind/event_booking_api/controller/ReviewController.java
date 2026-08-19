package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;
import com.lhind.event_booking_api.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(
        name = "Reviews",
        description = "Endpoints for creating, retrieving, updating and deleting event reviews"
)
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService
    ) {
        this.reviewService = reviewService;
    }

    // USER / ORGANIZER / ADMIN
    @Operation(
            summary = "Create review",
            description = "Creates a review for an event. The user must have attended the event and the event must have ended"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Review created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Review cannot be created because event has not ended or user has not attended it"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User has already reviewed this event"
            )
    })
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
    @Operation(
            summary = "Get review by id",
            description = "Returns a review by its id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Review retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewById(
            @PathVariable Long reviewId
    ) {

        return ResponseEntity.ok(
                reviewService.getReviewById(reviewId)
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get reviews by event",
            description = "Returns all reviews for a specific event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reviews retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByEvent(
            @PathVariable Long eventId
    ) {

        return ResponseEntity.ok(
                reviewService.getReviewsByEvent(eventId)
        );
    }

    // AUTHENTICATED
    @Operation(
            summary = "Get my reviews",
            description = "Returns all reviews created by the currently authenticated user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Reviews retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<List<ReviewResponse>> getMyReviews() {

        return ResponseEntity.ok(
                reviewService.getMyReviews()
        );
    }

    // OWNER / ADMIN
    @Operation(
            summary = "Update review",
            description = "Updates a review. Accessible by the review owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Review updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Review cannot be moved to another event or request data is invalid"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
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
    @Operation(
            summary = "Delete review",
            description = "Deletes a review. Accessible by the review owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Review deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Review not found"
            )
    })
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