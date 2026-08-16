package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    List<BookingSeat> findByBookingId(Long bookingId);

    List<BookingSeat> findByEventSeatId(Long eventSeatId);

    boolean existsByBookingIdAndEventSeatId(
            Long bookingId,
            Long eventSeatId
    );
}