package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Booking;
import com.lhind.event_booking_api.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Native Query
    @Query(
            value = """
                    SELECT *
                    FROM bookings
                    WHERE user_id = :userId
                    """,
            nativeQuery = true
    )
    List<Booking> findBookingsByUserNative(
            @Param("userId") Long userId
    );

    // Derived Query Method
    List<Booking> findByEventId(Long eventId);

    // Derived Query Method
    List<Booking> findByUserIdAndBookingStatus(
            Long userId,
            BookingStatus bookingStatus
    );

    // Derived Query Method
    List<Booking> findByEventIdAndBookingStatus(
            Long eventId,
            BookingStatus bookingStatus
    );

    // Derived Query Method
    boolean existsByUserIdAndEventId(
            Long userId,
            Long eventId
    );

    // Derived Query Method
    boolean existsByUserIdAndEventIdAndBookingStatus(
            Long userId,
            Long eventId,
            BookingStatus bookingStatus
    );
}