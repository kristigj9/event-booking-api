package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Booking;
import com.lhind.event_booking_api.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByUserIdAndBookingStatus(
            Long userId,
            BookingStatus bookingStatus
    );

    List<Booking> findByEventIdAndBookingStatus(
            Long eventId,
            BookingStatus bookingStatus
    );

    boolean existsByUserIdAndEventId(
            Long userId,
            Long eventId
    );

    boolean existsByUserIdAndEventIdAndBookingStatus(
            Long userId,
            Long eventId,
            BookingStatus bookingStatus
    );
}