package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.review.ReviewRequest;
import com.lhind.event_booking_api.dto.review.ReviewResponse;
import com.lhind.event_booking_api.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private ReviewRequest reviewRequest;

    private ReviewResponse reviewResponse;

    @BeforeEach
    void setUp() {

        ReviewController reviewController =
                new ReviewController(reviewService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(reviewController)
                .build();

        objectMapper = new ObjectMapper();

        reviewRequest = new ReviewRequest();

        reviewRequest.setRatingReview(5);
        reviewRequest.setCommentReview(
                "Great event"
        );
        reviewRequest.setEventId(1L);

        reviewResponse =
                ReviewResponse.builder()
                        .id(1L)
                        .ratingReview(5)
                        .commentReview(
                                "Great event"
                        )
                        .dateTimeReview(
                                LocalDateTime.now()
                        )
                        .userId(1L)
                        .eventId(1L)
                        .build();
    }

    @Test
    void createReview_shouldReturnCreated()
            throws Exception {

        when(reviewService.createReview(
                any(ReviewRequest.class)
        )).thenReturn(reviewResponse);

        mockMvc.perform(
                        post("/api/reviews")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                reviewRequest
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.ratingReview")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$.commentReview")
                                .value("Great event")
                )
                .andExpect(
                        jsonPath("$.eventId")
                                .value(1)
                );

        verify(reviewService, times(1))
                .createReview(
                        any(ReviewRequest.class)
                );
    }

    @Test
    void getReviewById_shouldReturnOk()
            throws Exception {

        when(reviewService.getReviewById(1L))
                .thenReturn(reviewResponse);

        mockMvc.perform(
                        get("/api/reviews/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.ratingReview")
                                .value(5)
                );

        verify(reviewService, times(1))
                .getReviewById(1L);
    }

    @Test
    void getReviewsByEvent_shouldReturnOk()
            throws Exception {

        when(reviewService.getReviewsByEvent(1L))
                .thenReturn(
                        List.of(reviewResponse)
                );

        mockMvc.perform(
                        get("/api/reviews/event/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].eventId")
                                .value(1)
                );

        verify(reviewService, times(1))
                .getReviewsByEvent(1L);
    }

    @Test
    void getMyReviews_shouldReturnOk()
            throws Exception {

        when(reviewService.getMyReviews())
                .thenReturn(
                        List.of(reviewResponse)
                );

        mockMvc.perform(
                        get("/api/reviews/me")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                );

        verify(reviewService, times(1))
                .getMyReviews();
    }

    @Test
    void updateReview_shouldReturnOk()
            throws Exception {

        ReviewRequest updateRequest =
                new ReviewRequest();

        updateRequest.setRatingReview(4);
        updateRequest.setCommentReview(
                "Very good event"
        );
        updateRequest.setEventId(1L);

        ReviewResponse updatedResponse =
                ReviewResponse.builder()
                        .id(1L)
                        .ratingReview(4)
                        .commentReview(
                                "Very good event"
                        )
                        .userId(1L)
                        .eventId(1L)
                        .build();

        when(reviewService.updateReview(
                eq(1L),
                any(ReviewRequest.class)
        )).thenReturn(updatedResponse);

        mockMvc.perform(
                        put("/api/reviews/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                updateRequest
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.ratingReview")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.commentReview")
                                .value(
                                        "Very good event"
                                )
                );

        verify(reviewService, times(1))
                .updateReview(
                        eq(1L),
                        any(ReviewRequest.class)
                );
    }

    @Test
    void deleteReview_shouldReturnNoContent()
            throws Exception {

        mockMvc.perform(
                        delete("/api/reviews/1")
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(reviewService, times(1))
                .deleteReview(1L);
    }

    @Test
    void createReview_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        ReviewRequest invalidRequest =
                new ReviewRequest();

        mockMvc.perform(
                        post("/api/reviews")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(reviewService, never())
                .createReview(
                        any(ReviewRequest.class)
                );
    }

    @Test
    void updateReview_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        ReviewRequest invalidRequest =
                new ReviewRequest();

        mockMvc.perform(
                        put("/api/reviews/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(reviewService, never())
                .updateReview(
                        eq(1L),
                        any(ReviewRequest.class)
                );
    }
}