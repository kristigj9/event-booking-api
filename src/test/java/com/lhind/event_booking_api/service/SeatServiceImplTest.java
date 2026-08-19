package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.reference.VenueReferenceRequest;
import com.lhind.event_booking_api.dto.seat.SeatRequest;
import com.lhind.event_booking_api.dto.seat.SeatResponse;
import com.lhind.event_booking_api.entity.Seat;
import com.lhind.event_booking_api.entity.Venue;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.SeatMapper;
import com.lhind.event_booking_api.repository.SeatRepository;
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
class SeatServiceImplTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private SeatMapper seatMapper;

    @InjectMocks
    private SeatServiceImpl seatService;

    private Venue venue;
    private Seat seat;
    private SeatRequest request;
    private SeatResponse response;

    @BeforeEach
    void setUp() {

        venue = Venue.builder()
                .id(1L)
                .venueName("Tirana Arena")
                .venueAddress("Tirana")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        VenueReferenceRequest venueRequest =
                new VenueReferenceRequest();

        venueRequest.setVenueName("Tirana Arena");
        venueRequest.setVenueCity("Tirana");

        request = new SeatRequest();
        request.setRowNumber("A");
        request.setSeatNumber(1);
        request.setVenue(venueRequest);

        seat = Seat.builder()
                .id(1L)
                .rowNumber("A")
                .seatNumber(1)
                .venue(venue)
                .build();

        response = SeatResponse.builder()
                .id(1L)
                .rowNumber("A")
                .seatNumber(1)
                .venueId(1L)
                .build();
    }

    @Test
    void createSeat_shouldCreateSeatSuccessfully() {

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(seatRepository
                .existsByVenueIdAndRowNumberAndSeatNumber(
                        1L,
                        "A",
                        1
                ))
                .thenReturn(false);

        when(seatMapper.toEntity(
                request,
                venue
        )).thenReturn(seat);

        when(seatRepository.save(seat))
                .thenReturn(seat);

        when(seatMapper.toResponse(seat))
                .thenReturn(response);

        SeatResponse result =
                seatService.createSeat(request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(seatRepository, times(1))
                .save(seat);
    }

    @Test
    void createSeat_shouldThrowExceptionWhenVenueNotFound() {

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> seatService.createSeat(request)
                );

        assertEquals(
                "Venue not found: Tirana Arena",
                exception.getMessage()
        );

        verify(seatRepository, never())
                .save(any(Seat.class));
    }

    @Test
    void createSeat_shouldThrowExceptionWhenSeatAlreadyExists() {

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(seatRepository
                .existsByVenueIdAndRowNumberAndSeatNumber(
                        1L,
                        "A",
                        1
                ))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> seatService.createSeat(request)
                );

        assertEquals(
                "Seat already exists in this venue",
                exception.getMessage()
        );

        verify(seatRepository, never())
                .save(any(Seat.class));
    }

    @Test
    void getSeatById_shouldReturnSeatSuccessfully() {

        when(seatRepository.findById(1L))
                .thenReturn(Optional.of(seat));

        when(seatMapper.toResponse(seat))
                .thenReturn(response);

        SeatResponse result =
                seatService.getSeatById(1L);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void getSeatById_shouldThrowExceptionWhenSeatNotFound() {

        when(seatRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> seatService.getSeatById(99L)
                );

        assertEquals(
                "Seat not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void getAllSeats_shouldReturnAllSeats() {

        List<Seat> seats =
                List.of(seat);

        List<SeatResponse> responses =
                List.of(response);

        when(seatRepository.findAll())
                .thenReturn(seats);

        when(seatMapper.toResponseList(seats))
                .thenReturn(responses);

        List<SeatResponse> result =
                seatService.getAllSeats();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSeatsByVenue_shouldReturnSeatsSuccessfully() {

        List<Seat> seats =
                List.of(seat);

        List<SeatResponse> responses =
                List.of(response);

        when(venueRepository.existsById(1L))
                .thenReturn(true);

        when(seatRepository.findByVenueId(1L))
                .thenReturn(seats);

        when(seatMapper.toResponseList(seats))
                .thenReturn(responses);

        List<SeatResponse> result =
                seatService.getSeatsByVenue(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSeatsByVenue_shouldThrowExceptionWhenVenueNotFound() {

        when(venueRepository.existsById(99L))
                .thenReturn(false);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> seatService.getSeatsByVenue(99L)
                );

        assertEquals(
                "Venue not found with id: 99",
                exception.getMessage()
        );

        verify(seatRepository, never())
                .findByVenueId(anyLong());
    }

    @Test
    void updateSeat_shouldUpdateSeatSuccessfully() {

        when(seatRepository.findById(1L))
                .thenReturn(Optional.of(seat));

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(seatRepository.save(seat))
                .thenReturn(seat);

        when(seatMapper.toResponse(seat))
                .thenReturn(response);

        SeatResponse result =
                seatService.updateSeat(
                        1L,
                        request
                );

        assertNotNull(result);

        verify(seatMapper, times(1))
                .updateEntity(
                        request,
                        seat,
                        venue
                );

        verify(seatRepository, times(1))
                .save(seat);
    }

    @Test
    void updateSeat_shouldThrowExceptionWhenNewSeatAlreadyExists() {

        SeatRequest updateRequest =
                new SeatRequest();

        VenueReferenceRequest venueRequest =
                new VenueReferenceRequest();

        venueRequest.setVenueName("Tirana Arena");
        venueRequest.setVenueCity("Tirana");

        updateRequest.setRowNumber("A");
        updateRequest.setSeatNumber(2);
        updateRequest.setVenue(venueRequest);

        when(seatRepository.findById(1L))
                .thenReturn(Optional.of(seat));

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(seatRepository
                .existsByVenueIdAndRowNumberAndSeatNumber(
                        1L,
                        "A",
                        2
                ))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> seatService.updateSeat(
                                1L,
                                updateRequest
                        )
                );

        assertEquals(
                "Seat already exists in this venue",
                exception.getMessage()
        );

        verify(seatRepository, never())
                .save(any(Seat.class));
    }

    @Test
    void deleteSeat_shouldDeleteSeatSuccessfully() {

        when(seatRepository.findById(1L))
                .thenReturn(Optional.of(seat));

        seatService.deleteSeat(1L);

        verify(seatRepository, times(1))
                .delete(seat);
    }

    @Test
    void deleteSeat_shouldThrowExceptionWhenSeatNotFound() {

        when(seatRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> seatService.deleteSeat(99L)
                );

        assertEquals(
                "Seat not found with id: 99",
                exception.getMessage()
        );

        verify(seatRepository, never())
                .delete(any(Seat.class));
    }


}