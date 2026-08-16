package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.EventSeat;
import com.lhind.event_booking_api.entity.StatusSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

    List<EventSeat> findByEventId(Long eventId);

    List<EventSeat> findByEventIdAndStatusSeat(
            Long eventId,
            StatusSeat statusSeat
    );

    Optional<EventSeat> findByEventIdAndSeatId(
            Long eventId,
            Long seatId
    );
}