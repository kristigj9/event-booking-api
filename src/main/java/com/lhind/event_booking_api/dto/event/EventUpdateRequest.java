package com.lhind.event_booking_api.dto.event;

import com.lhind.event_booking_api.dto.reference.CategoryReferenceRequest;
import com.lhind.event_booking_api.dto.reference.VenueReferenceRequest;
import com.lhind.event_booking_api.entity.EventStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventUpdateRequest {

    @NotBlank(message = "Event name is required")
    @Size(
            min = 2,
            max = 150,
            message = "Event name must be between 2 and 150 characters"
    )
    private String eventName;

    @Size(
            max = 1000,
            message = "Event description must not exceed 1000 characters"
    )
    private String eventDescription;

    @NotNull(message = "Event start date and time is required")
    private LocalDateTime eventStartDateTime;

    @NotNull(message = "Event end date and time is required")
    private LocalDateTime eventEndDateTime;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be greater than 0")
    private Integer eventTotalSeats;

    @NotNull(message = "Event status is required")
    private EventStatus eventStatus;

    @Valid
    @NotNull(message = "Venue is required")
    private VenueReferenceRequest venue;

    @Valid
    @NotEmpty(message = "At least one category is required")
    private List<CategoryReferenceRequest> categories;
}