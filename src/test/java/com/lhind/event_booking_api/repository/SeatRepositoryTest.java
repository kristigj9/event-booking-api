package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Seat;
import com.lhind.event_booking_api.entity.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Venue venue;
    private Seat seat1;
    private Seat seat2;

    @BeforeEach
    void setUp() {

        venue = Venue.builder()
                .venueName("Seat Test Arena")
                .venueAddress("Tirana")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        venue = venueRepository.save(venue);

        seat1 = Seat.builder()
                .rowNumber("A")
                .seatNumber(1)
                .venue(venue)
                .build();

        seat2 = Seat.builder()
                .rowNumber("A")
                .seatNumber(2)
                .venue(venue)
                .build();

        seat1 = seatRepository.save(seat1);
        seat2 = seatRepository.save(seat2);
    }

    @Test
    void findByVenueId_shouldReturnVenueSeats() {

        List<Seat> result =
                seatRepository.findByVenueId(
                        venue.getId()
                );

        assertEquals(
                2,
                result.size()
        );

        assertTrue(
                result.stream()
                        .allMatch(seat ->
                                seat.getVenue()
                                        .getId()
                                        .equals(venue.getId())
                        )
        );
    }

    @Test
    void existsByVenueIdAndRowNumberAndSeatNumber_shouldReturnTrue() {

        boolean result =
                seatRepository
                        .existsByVenueIdAndRowNumberAndSeatNumber(
                                venue.getId(),
                                "A",
                                1
                        );

        assertTrue(result);
    }

    @Test
    void existsByVenueIdAndRowNumberAndSeatNumber_shouldReturnFalse() {

        boolean result =
                seatRepository
                        .existsByVenueIdAndRowNumberAndSeatNumber(
                                venue.getId(),
                                "Z",
                                99
                        );

        assertFalse(result);
    }

    @Test
    void findByVenueIdAndRowNumberAndSeatNumber_shouldReturnSeat() {

        Optional<Seat> result =
                seatRepository
                        .findByVenueIdAndRowNumberAndSeatNumber(
                                venue.getId(),
                                "A",
                                1
                        );

        assertTrue(result.isPresent());

        assertEquals(
                "A",
                result.get().getRowNumber()
        );

        assertEquals(
                1,
                result.get().getSeatNumber()
        );

        assertEquals(
                venue.getId(),
                result.get()
                        .getVenue()
                        .getId()
        );
    }

    @Test
    void findByVenueIdAndRowNumberAndSeatNumber_shouldReturnEmpty() {

        Optional<Seat> result =
                seatRepository
                        .findByVenueIdAndRowNumberAndSeatNumber(
                                venue.getId(),
                                "B",
                                50
                        );

        assertTrue(result.isEmpty());
    }
}