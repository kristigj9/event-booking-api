package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.venue.VenueRequest;
import com.lhind.event_booking_api.dto.venue.VenueResponse;
import com.lhind.event_booking_api.service.VenueService;
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
class VenueControllerTest {

    @Mock
    private VenueService venueService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private VenueRequest venueRequest;

    private VenueResponse venueResponse;

    @BeforeEach
    void setUp() {

        VenueController venueController =
                new VenueController(venueService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(venueController)
                .build();

        objectMapper = new ObjectMapper();

        venueRequest = new VenueRequest();

        venueRequest.setVenueName(
                "Tirana Arena"
        );

        venueRequest.setVenueAddress(
                "Bulevardi Deshmoret e Kombit"
        );

        venueRequest.setVenueCity(
                "Tirana"
        );

        venueRequest.setVenueCapacity(
                100
        );

        venueResponse =
                VenueResponse.builder()
                        .id(1L)
                        .venueName(
                                "Tirana Arena"
                        )
                        .venueAddress(
                                "Bulevardi Deshmoret e Kombit"
                        )
                        .venueCity(
                                "Tirana"
                        )
                        .venueCapacity(
                                100
                        )
                        .build();
    }

    @Test
    void createVenue_shouldReturnCreated()
            throws Exception {

        when(venueService.createVenue(
                any(VenueRequest.class)
        )).thenReturn(venueResponse);

        mockMvc.perform(
                        post("/api/venues")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                venueRequest
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
                        jsonPath("$.venueName")
                                .value(
                                        "Tirana Arena"
                                )
                )
                .andExpect(
                        jsonPath("$.venueCity")
                                .value(
                                        "Tirana"
                                )
                )
                .andExpect(
                        jsonPath("$.venueCapacity")
                                .value(100)
                );

        verify(venueService, times(1))
                .createVenue(
                        any(VenueRequest.class)
                );
    }

    @Test
    void getAllVenues_shouldReturnOk()
            throws Exception {

        when(venueService.getAllVenues())
                .thenReturn(
                        List.of(venueResponse)
                );

        mockMvc.perform(
                        get("/api/venues")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].venueName")
                                .value(
                                        "Tirana Arena"
                                )
                );

        verify(venueService, times(1))
                .getAllVenues();
    }

    @Test
    void getVenueById_shouldReturnOk()
            throws Exception {

        when(venueService.getVenueById(1L))
                .thenReturn(venueResponse);

        mockMvc.perform(
                        get("/api/venues/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.venueName")
                                .value(
                                        "Tirana Arena"
                                )
                );

        verify(venueService, times(1))
                .getVenueById(1L);
    }

    @Test
    void updateVenue_shouldReturnOk()
            throws Exception {

        VenueResponse updatedResponse =
                VenueResponse.builder()
                        .id(1L)
                        .venueName(
                                "Updated Arena"
                        )
                        .venueAddress(
                                "Updated Address"
                        )
                        .venueCity(
                                "Tirana"
                        )
                        .venueCapacity(
                                150
                        )
                        .build();

        VenueRequest updateRequest =
                new VenueRequest();

        updateRequest.setVenueName(
                "Updated Arena"
        );

        updateRequest.setVenueAddress(
                "Updated Address"
        );

        updateRequest.setVenueCity(
                "Tirana"
        );

        updateRequest.setVenueCapacity(
                150
        );

        when(venueService.updateVenue(
                eq(1L),
                any(VenueRequest.class)
        )).thenReturn(updatedResponse);

        mockMvc.perform(
                        put("/api/venues/1")
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
                        jsonPath("$.venueName")
                                .value(
                                        "Updated Arena"
                                )
                )
                .andExpect(
                        jsonPath("$.venueCapacity")
                                .value(150)
                );

        verify(venueService, times(1))
                .updateVenue(
                        eq(1L),
                        any(VenueRequest.class)
                );
    }

    @Test
    void deleteVenue_shouldReturnNoContent()
            throws Exception {

        mockMvc.perform(
                        delete("/api/venues/1")
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(venueService, times(1))
                .deleteVenue(1L);
    }

    @Test
    void createVenue_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        VenueRequest invalidRequest =
                new VenueRequest();

        mockMvc.perform(
                        post("/api/venues")
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

        verify(venueService, never())
                .createVenue(
                        any(VenueRequest.class)
                );
    }

    @Test
    void updateVenue_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        VenueRequest invalidRequest =
                new VenueRequest();

        mockMvc.perform(
                        put("/api/venues/1")
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

        verify(venueService, never())
                .updateVenue(
                        eq(1L),
                        any(VenueRequest.class)
                );
    }
}