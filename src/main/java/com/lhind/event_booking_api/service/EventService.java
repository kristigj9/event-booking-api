package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.event.EventRequest;
import com.lhind.event_booking_api.dto.event.EventResponse;
import com.lhind.event_booking_api.entity.EventStatus;

import java.util.List;

public interface EventService {

    EventResponse createEvent(
            EventRequest request,
            Long organizerId
    );

    EventResponse getEventById(Long id);

    List<EventResponse> getAllEvents();

    List<EventResponse> getEventsByOrganizer(Long organizerId);

    List<EventResponse> getEventsByStatus(EventStatus status);

    List<EventResponse> getEventsByCategory(Long categoryId);

    List<EventResponse> getEventsByVenue(Long venueId);

    EventResponse updateEvent(
            Long eventId,
            Long organizerId,
            EventRequest request
    );

    void deleteEvent(
            Long eventId,
            Long organizerId
    );
}