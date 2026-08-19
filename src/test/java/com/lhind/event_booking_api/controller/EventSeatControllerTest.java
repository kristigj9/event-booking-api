package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.eventseat.EventSeatRequest;
import com.lhind.event_booking_api.dto.eventseat.EventSeatResponse;
import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import com.lhind.event_booking_api.entity.StatusSeat;
import com.lhind.event_booking_api.service.EventSeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventSeatControllerTest {

    @Mock
    private EventSeatService eventSeatService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private EventSeatRequest eventSeatRequest;

    private EventSeatResponse eventSeatResponse;

    @BeforeEach
    void setUp() {

        EventSeatController eventSeatController =
                new EventSeatController(eventSeatService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(eventSeatController)
                .build();

        objectMapper = new ObjectMapper();

        SeatSelectionRequest seatRequest =
                new SeatSelectionRequest();

        seatRequest.setRowNumber("A");
        seatRequest.setSeatNumber(1);

        eventSeatRequest =
                new EventSeatRequest();

        eventSeatRequest.setSeat(
                seatRequest
        );

        eventSeatRequest.setPriceSeat(
                new BigDecimal("25.00")
        );

        eventSeatResponse =
                EventSeatResponse.builder()
                        .id(1L)
                        .statusSeat(
                                StatusSeat.AVAILABLE
                        )
                        .priceSeat(
                                new BigDecimal("25.00")
                        )
                        .build();
    }

    @Test
    void createEventSeat_shouldReturnCreated()
            throws Exception {

        when(eventSeatService.createEventSeat(
                eq(1L),
                any(EventSeatRequest.class)
        )).thenReturn(eventSeatResponse);

        mockMvc.perform(
                        post("/api/event-seats/event/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                eventSeatRequest
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
                        jsonPath("$.statusSeat")
                                .value("AVAILABLE")
                )
                .andExpect(
                        jsonPath("$.priceSeat")
                                .value(25.00)
                );

        verify(eventSeatService, times(1))
                .createEventSeat(
                        eq(1L),
                        any(EventSeatRequest.class)
                );
    }

    @Test
    void getEventSeatById_shouldReturnOk()
            throws Exception {

        when(eventSeatService.getEventSeatById(1L))
                .thenReturn(
                        eventSeatResponse
                );

        mockMvc.perform(
                        get("/api/event-seats/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.statusSeat")
                                .value("AVAILABLE")
                );

        verify(eventSeatService, times(1))
                .getEventSeatById(1L);
    }

    @Test
    void getSeatsByEvent_shouldReturnOk()
            throws Exception {

        when(eventSeatService.getSeatsByEvent(1L))
                .thenReturn(
                        List.of(eventSeatResponse)
                );

        mockMvc.perform(
                        get("/api/event-seats/event/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].statusSeat")
                                .value("AVAILABLE")
                );

        verify(eventSeatService, times(1))
                .getSeatsByEvent(1L);
    }

    @Test
    void getSeatsByEventAndStatus_shouldReturnOk()
            throws Exception {

        when(eventSeatService.getSeatsByEventAndStatus(
                1L,
                StatusSeat.AVAILABLE
        )).thenReturn(
                List.of(eventSeatResponse)
        );

        mockMvc.perform(
                        get(
                                "/api/event-seats/event/1/status/AVAILABLE"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].statusSeat")
                                .value("AVAILABLE")
                );

        verify(eventSeatService, times(1))
                .getSeatsByEventAndStatus(
                        1L,
                        StatusSeat.AVAILABLE
                );
    }

    @Test
    void updatePrice_shouldReturnOk()
            throws Exception {

        BigDecimal newPrice =
                new BigDecimal("35.00");

        EventSeatResponse updatedResponse =
                EventSeatResponse.builder()
                        .id(1L)
                        .statusSeat(
                                StatusSeat.AVAILABLE
                        )
                        .priceSeat(
                                newPrice
                        )
                        .build();

        when(eventSeatService.updatePrice(
                1L,
                newPrice
        )).thenReturn(
                updatedResponse
        );

        mockMvc.perform(
                        patch(
                                "/api/event-seats/1/price"
                        )
                                .param(
                                        "priceSeat",
                                        "35.00"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.priceSeat")
                                .value(35.00)
                )
                .andExpect(
                        jsonPath("$.statusSeat")
                                .value("AVAILABLE")
                );

        verify(eventSeatService, times(1))
                .updatePrice(
                        1L,
                        new BigDecimal("35.00")
                );
    }

    @Test
    void deleteEventSeat_shouldReturnNoContent()
            throws Exception {

        mockMvc.perform(
                        delete(
                                "/api/event-seats/1"
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(eventSeatService, times(1))
                .deleteEventSeat(1L);
    }

    @Test
    void createEventSeat_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        EventSeatRequest invalidRequest =
                new EventSeatRequest();

        mockMvc.perform(
                        post("/api/event-seats/event/1")
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

        verify(eventSeatService, never())
                .createEventSeat(
                        eq(1L),
                        any(EventSeatRequest.class)
                );
    }

    @Test
    void updatePrice_shouldReturnBadRequestWhenPriceIsInvalid()
            throws Exception {

        mockMvc.perform(
                        patch(
                                "/api/event-seats/1/price"
                        )
                                .param(
                                        "priceSeat",
                                        "invalid"
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(eventSeatService, never())
                .updatePrice(
                        eq(1L),
                        any(BigDecimal.class)
                );
    }
}