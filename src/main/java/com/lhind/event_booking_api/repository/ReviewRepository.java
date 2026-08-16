package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByEventId(Long eventId);

    List<Review> findByUserId(Long userId);

    Optional<Review> findByUserIdAndEventId(
            Long userId,
            Long eventId
    );

    boolean existsByUserIdAndEventId(
            Long userId,
            Long eventId
    );
}