package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Derived Query Method
    List<Event> findByOrganizerId(Long organizerId);

    // Derived Query Method
    List<Event> findByCategoriesId(Long categoryId);

    // Derived Query Method
    List<Event> findByVenueId(Long venueId);

    // Derived Query Method
    List<Event> findByOrganizerIdAndEventStatus(
            Long organizerId,
            EventStatus eventStatus
    );

    // JPQL Query
    @Query("""
            SELECT e
            FROM Event e
            WHERE e.eventStatus = :status
            """)
    List<Event> findEventsByStatusJPQL(
            @Param("status") EventStatus status
    );
}