package com.lhind.event_booking_api.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    private Integer ratingReview;

    private String commentReview;

    private Long eventId;
}