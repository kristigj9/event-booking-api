package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByVenueNameAndVenueCity(
            String venueName,
            String venueCity
    );
}