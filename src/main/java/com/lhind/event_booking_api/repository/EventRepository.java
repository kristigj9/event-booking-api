package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizerId(Long organizerId);

    List<Event> findByEventStatus(EventStatus eventStatus);

    List<Event> findByCategoryId(Long categoryId);

    List<Event> findByVenueId(Long venueId);

    List<Event> findByOrganizerIdAndEventStatus(
            Long organizerId,
            EventStatus eventStatus
    );
}