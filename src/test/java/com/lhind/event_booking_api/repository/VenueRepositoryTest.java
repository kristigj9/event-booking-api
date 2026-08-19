package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    private Venue venue;

    @BeforeEach
    void setUp() {

        venue = Venue.builder()
                .venueName("Tirana Arena")
                .venueAddress("Sheshi Italia")
                .venueCity("Tirana")
                .venueCapacity(500)
                .build();

        venue = venueRepository.save(venue);
    }

    @Test
    void findByVenueNameAndVenueCity_shouldReturnVenue() {

        Optional<Venue> result =
                venueRepository.findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                );

        assertTrue(result.isPresent());

        assertEquals(
                venue.getId(),
                result.get().getId()
        );

        assertEquals(
                "Tirana Arena",
                result.get().getVenueName()
        );

        assertEquals(
                "Tirana",
                result.get().getVenueCity()
        );

        assertEquals(
                500,
                result.get().getVenueCapacity()
        );
    }

    @Test
    void findByVenueNameAndVenueCity_shouldReturnEmptyWhenNameDoesNotExist() {

        Optional<Venue> result =
                venueRepository.findByVenueNameAndVenueCity(
                        "Unknown Arena",
                        "Tirana"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void findByVenueNameAndVenueCity_shouldReturnEmptyWhenCityDoesNotMatch() {

        Optional<Venue> result =
                venueRepository.findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Durres"
                );

        assertTrue(result.isEmpty());
    }
}