package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByVenueId(Long venueId);
    // Kontrollojmë nese nje Seat ekziston brenda nje Venue
    boolean existsByVenueIdAndRowNumberAndSeatNumber(
            Long venueId,
            String rowNumber,
            Integer seatNumber
    );

    // Marrim Seat-in konkret brenda nje Venue
    Optional<Seat> findByVenueIdAndRowNumberAndSeatNumber(
            Long venueId,
            String rowNumber,
            Integer seatNumber
    );
}