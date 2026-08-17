package com.lhind.event_booking_api.dto.review;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer ratingReview;

    @Size(max = 1000,
            message = "Review comment must not exceed 1000 characters")
    private String commentReview;

    @NotNull(message = "Event id is required")
    @Positive(message = "Event id must be greater than 0")
    private Long eventId;
}