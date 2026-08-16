package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}