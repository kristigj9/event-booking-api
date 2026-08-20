package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Waitlist;
import com.lhind.event_booking_api.entity.WaitlistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    // Kontrollon nese user-i eshte tashme waitlist per eventin
    boolean existsByUserIdAndEventId(
            Long userId,
            Long eventId
    );

    // Gjen waitlist entry te nje user-i per nje event
    Optional<Waitlist> findByUserIdAndEventId(
            Long userId,
            Long eventId
    );

    // Merr waitlist-in e nje eventi, I pari qe do trajtohet nga lista eshte ai qe ka kaluar i pari
    List<Waitlist> findByEventIdOrderByCreatedAtAsc(
            Long eventId
    );

    // Merr waitlist entries e user-it
    List<Waitlist> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    // Merr waitlist sipas eventit dhe statusit
    List<Waitlist> findByEventIdAndStatusOrderByCreatedAtAsc(
            Long eventId,
            WaitlistStatus status
    );
}