package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.event.EventRequest;
import com.lhind.event_booking_api.dto.event.EventResponse;
import com.lhind.event_booking_api.dto.event.EventUpdateRequest;
import com.lhind.event_booking_api.dto.reference.EventResponseShort;
import com.lhind.event_booking_api.entity.Category;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.EventStatus;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.entity.Venue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class EventMapper {

    private final UserMapper userMapper;
    private final VenueMapper venueMapper;
    private final CategoryMapper categoryMapper;

    public EventMapper(
            UserMapper userMapper,
            VenueMapper venueMapper,
            CategoryMapper categoryMapper
    ) {
        this.userMapper = userMapper;
        this.venueMapper = venueMapper;
        this.categoryMapper = categoryMapper;
    }

    public Event toEntity(
            EventRequest request,
            User organizer,
            Venue venue,
            List<Category> categories
    ) {

        if (request == null) {
            return null;
        }

        return Event.builder()
                .eventName(request.getEventName())
                .eventDescription(request.getEventDescription())
                .eventStartDateTime(request.getEventStartDateTime())
                .eventEndDateTime(request.getEventEndDateTime())
                .eventTotalSeats(request.getEventTotalSeats())
                .eventAvailableSeats(request.getEventTotalSeats())
                .eventStatus(EventStatus.DRAFT)
                .organizer(organizer)
                .venue(venue)
                .categories(new ArrayList<>(categories))
                .build();
    }

    public EventResponse toResponse(
            Event event
    ) {

        if (event == null) {
            return null;
        }

        return EventResponse.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .eventStartDateTime(event.getEventStartDateTime())
                .eventEndDateTime(event.getEventEndDateTime())
                .eventTotalSeats(event.getEventTotalSeats())
                .eventAvailableSeats(event.getEventAvailableSeats())
                .eventStatus(event.getEventStatus())
                .organizer(
                        userMapper.toShortResponse(
                                event.getOrganizer()
                        )
                )
                .venue(
                        venueMapper.toShortResponse(
                                event.getVenue()
                        )
                )
                .categories(
                        categoryMapper.toShortResponseList(
                                event.getCategories()
                        )
                )
                .build();
    }

    public EventResponseShort toShortResponse(
            Event event
    ) {

        if (event == null) {
            return null;
        }

        return EventResponseShort.builder()
                .id(event.getId())
                .eventName(event.getEventName())
                .eventStartDateTime(
                        event.getEventStartDateTime()
                )
                .eventEndDateTime(
                        event.getEventEndDateTime()
                )
                .eventStatus(
                        event.getEventStatus()
                )
                .build();
    }

    public List<EventResponse> toResponseList(
            List<Event> events
    ) {

        if (events == null) {
            return Collections.emptyList();
        }

        return events.stream()
                .map(this::toResponse)
                .toList();
    }

    // Update i Event ekzistues
    public void updateEntity(
            EventUpdateRequest request,
            Event event,
            Venue venue,
            List<Category> categories
    ) {

        if (request == null || event == null) {
            return;
        }

        event.setEventName(
                request.getEventName()
        );

        event.setEventDescription(
                request.getEventDescription()
        );

        event.setEventStartDateTime(
                request.getEventStartDateTime()
        );

        event.setEventEndDateTime(
                request.getEventEndDateTime()
        );

        event.setEventTotalSeats(
                request.getEventTotalSeats()
        );

        event.setEventStatus(
                request.getEventStatus()
        );

        event.setVenue(venue);

        event.getCategories().clear();
        event.getCategories().addAll(categories);
    }
}