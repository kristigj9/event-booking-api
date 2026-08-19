package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.reference.VenueReferenceRequest;
import com.lhind.event_booking_api.dto.seat.SeatRequest;
import com.lhind.event_booking_api.dto.seat.SeatResponse;
import com.lhind.event_booking_api.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SeatControllerTest {

    @Mock
    private SeatService seatService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private SeatRequest seatRequest;

    private SeatResponse seatResponse;

    @BeforeEach
    void setUp() {

        SeatController seatController =
                new SeatController(seatService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(seatController)
                .build();

        objectMapper = new ObjectMapper();

        VenueReferenceRequest venueRequest =
                new VenueReferenceRequest();

        venueRequest.setVenueName("Tirana Arena");
        venueRequest.setVenueCity("Tirana");

        seatRequest = new SeatRequest();
        seatRequest.setRowNumber("A");
        seatRequest.setSeatNumber(1);
        seatRequest.setVenue(venueRequest);

        seatResponse =
                SeatResponse.builder()
                        .id(1L)
                        .rowNumber("A")
                        .seatNumber(1)
                        .venueId(1L)
                        .build();
    }

    @Test
    void createSeat_shouldReturnCreated()
            throws Exception {

        when(seatService.createSeat(
                any(SeatRequest.class)
        )).thenReturn(seatResponse);

        mockMvc.perform(
                        post("/api/seats")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                seatRequest
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.rowNumber")
                                .value("A")
                )
                .andExpect(
                        jsonPath("$.seatNumber")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.venueId")
                                .value(1)
                );

        verify(seatService, times(1))
                .createSeat(
                        any(SeatRequest.class)
                );
    }

    @Test
    void getAllSeats_shouldReturnOk()
            throws Exception {

        when(seatService.getAllSeats())
                .thenReturn(
                        List.of(seatResponse)
                );

        mockMvc.perform(
                        get("/api/seats")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].rowNumber")
                                .value("A")
                )
                .andExpect(
                        jsonPath("$[0].seatNumber")
                                .value(1)
                );

        verify(seatService, times(1))
                .getAllSeats();
    }

    @Test
    void getSeatById_shouldReturnOk()
            throws Exception {

        when(seatService.getSeatById(1L))
                .thenReturn(seatResponse);

        mockMvc.perform(
                        get("/api/seats/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.rowNumber")
                                .value("A")
                )
                .andExpect(
                        jsonPath("$.seatNumber")
                                .value(1)
                );

        verify(seatService, times(1))
                .getSeatById(1L);
    }

    @Test
    void getSeatsByVenue_shouldReturnOk()
            throws Exception {

        when(seatService.getSeatsByVenue(1L))
                .thenReturn(
                        List.of(seatResponse)
                );

        mockMvc.perform(
                        get("/api/seats/venue/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].venueId")
                                .value(1)
                );

        verify(seatService, times(1))
                .getSeatsByVenue(1L);
    }

    @Test
    void updateSeat_shouldReturnOk()
            throws Exception {

        VenueReferenceRequest venueRequest =
                new VenueReferenceRequest();

        venueRequest.setVenueName("Tirana Arena");
        venueRequest.setVenueCity("Tirana");

        SeatRequest updateRequest =
                new SeatRequest();

        updateRequest.setRowNumber("B");
        updateRequest.setSeatNumber(2);
        updateRequest.setVenue(venueRequest);

        SeatResponse updatedResponse =
                SeatResponse.builder()
                        .id(1L)
                        .rowNumber("B")
                        .seatNumber(2)
                        .venueId(1L)
                        .build();

        when(seatService.updateSeat(
                eq(1L),
                any(SeatRequest.class)
        )).thenReturn(updatedResponse);

        mockMvc.perform(
                        put("/api/seats/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                updateRequest
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.rowNumber")
                                .value("B")
                )
                .andExpect(
                        jsonPath("$.seatNumber")
                                .value(2)
                );

        verify(seatService, times(1))
                .updateSeat(
                        eq(1L),
                        any(SeatRequest.class)
                );
    }

    @Test
    void deleteSeat_shouldReturnNoContent()
            throws Exception {

        mockMvc.perform(
                        delete("/api/seats/1")
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(seatService, times(1))
                .deleteSeat(1L);
    }

    @Test
    void createSeat_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        SeatRequest invalidRequest =
                new SeatRequest();

        mockMvc.perform(
                        post("/api/seats")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(seatService, never())
                .createSeat(
                        any(SeatRequest.class)
                );
    }

    @Test
    void updateSeat_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        SeatRequest invalidRequest =
                new SeatRequest();

        mockMvc.perform(
                        put("/api/seats/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(seatService, never())
                .updateSeat(
                        eq(1L),
                        any(SeatRequest.class)
                );
    }
}