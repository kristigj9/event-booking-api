package com.lhind.event_booking_api.dto.review;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewResponse {

    private Long id;

    private Integer ratingReview;

    private String commentReview;

    private LocalDateTime dateTimeReview;

    private Long userId;

    private Long eventId;
}