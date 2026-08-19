package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.venue.VenueRequest;
import com.lhind.event_booking_api.dto.venue.VenueResponse;
import com.lhind.event_booking_api.entity.Venue;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.VenueMapper;
import com.lhind.event_booking_api.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceImplTest {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private VenueMapper venueMapper;

    @InjectMocks
    private VenueServiceImpl venueService;

    private Venue venue;
    private VenueRequest request;
    private VenueResponse response;

    @BeforeEach
    void setUp() {

        request = new VenueRequest();
        request.setVenueName("Tirana Arena");
        request.setVenueAddress("Bulevardi Deshmoret e Kombit");
        request.setVenueCity("Tirana");
        request.setVenueCapacity(100);

        venue = Venue.builder()
                .id(1L)
                .venueName("Tirana Arena")
                .venueAddress("Bulevardi Deshmoret e Kombit")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        response = VenueResponse.builder()
                .id(1L)
                .venueName("Tirana Arena")
                .venueAddress("Bulevardi Deshmoret e Kombit")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();
    }

    @Test
    void createVenue_shouldCreateVenueSuccessfully() {

        when(venueMapper.toEntity(request))
                .thenReturn(venue);

        when(venueRepository.save(venue))
                .thenReturn(venue);

        when(venueMapper.toResponse(venue))
                .thenReturn(response);

        VenueResponse result =
                venueService.createVenue(request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(venueRepository, times(1))
                .save(venue);
    }

    @Test
    void getVenueById_shouldReturnVenueSuccessfully() {

        when(venueRepository.findById(1L))
                .thenReturn(Optional.of(venue));

        when(venueMapper.toResponse(venue))
                .thenReturn(response);

        VenueResponse result =
                venueService.getVenueById(1L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(venueRepository, times(1))
                .findById(1L);
    }

    @Test
    void getVenueById_shouldThrowExceptionWhenVenueNotFound() {

        when(venueRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> venueService.getVenueById(99L)
                );

        assertEquals(
                "Venue not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void getAllVenues_shouldReturnAllVenues() {

        List<Venue> venues =
                List.of(venue);

        List<VenueResponse> responses =
                List.of(response);

        when(venueRepository.findAll())
                .thenReturn(venues);

        when(venueMapper.toResponseList(venues))
                .thenReturn(responses);

        List<VenueResponse> result =
                venueService.getAllVenues();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void updateVenue_shouldUpdateVenueSuccessfully() {

        when(venueRepository.findById(1L))
                .thenReturn(Optional.of(venue));

        when(venueRepository.save(venue))
                .thenReturn(venue);

        when(venueMapper.toResponse(venue))
                .thenReturn(response);

        VenueResponse result =
                venueService.updateVenue(
                        1L,
                        request
                );

        assertNotNull(result);

        verify(venueMapper, times(1))
                .updateEntity(
                        request,
                        venue
                );

        verify(venueRepository, times(1))
                .save(venue);
    }

    @Test
    void updateVenue_shouldThrowExceptionWhenVenueNotFound() {

        when(venueRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> venueService.updateVenue(
                                99L,
                                request
                        )
                );

        assertEquals(
                "Venue not found with id: 99",
                exception.getMessage()
        );

        verify(venueRepository, never())
                .save(any(Venue.class));
    }

    @Test
    void deleteVenue_shouldDeleteVenueSuccessfully() {

        when(venueRepository.findById(1L))
                .thenReturn(Optional.of(venue));

        venueService.deleteVenue(1L);

        verify(venueRepository, times(1))
                .delete(venue);
    }

    @Test
    void deleteVenue_shouldThrowExceptionWhenVenueNotFound() {

        when(venueRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> venueService.deleteVenue(99L)
                );

        assertEquals(
                "Venue not found with id: 99",
                exception.getMessage()
        );

        verify(venueRepository, never())
                .delete(any(Venue.class));
    }


}