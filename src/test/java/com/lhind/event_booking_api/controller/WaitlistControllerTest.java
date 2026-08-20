package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.WaitlistStatus;
import com.lhind.event_booking_api.security.CustomUserDetailsService;
import com.lhind.event_booking_api.security.JwtService;
import com.lhind.event_booking_api.service.WaitlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WaitlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WaitlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @MockitoBean
    private WaitlistService waitlistService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void joinWaitlist_shouldReturnCreated() throws Exception {

        WaitlistRequest request =
                new WaitlistRequest();

        request.setRequestedSeats(2);

        WaitlistResponse response =
                WaitlistResponse.builder()
                        .id(1L)
                        .requestedSeats(2)
                        .status(WaitlistStatus.WAITING)
                        .build();

        when(
                waitlistService.joinWaitlist(
                        eq(10L),
                        any(WaitlistRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/waitlists/event/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requestedSeats").value(2))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }


    @Test
    void getMyWaitlists_shouldReturnOk() throws Exception {

        WaitlistResponse response =
                WaitlistResponse.builder()
                        .id(1L)
                        .requestedSeats(2)
                        .status(WaitlistStatus.WAITING)
                        .build();

        when(waitlistService.getMyWaitlists())
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/waitlists/my")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }


    @Test
    void getWaitlistById_shouldReturnOk() throws Exception {

        WaitlistResponse response =
                WaitlistResponse.builder()
                        .id(1L)
                        .requestedSeats(2)
                        .status(WaitlistStatus.WAITING)
                        .build();

        when(waitlistService.getWaitlistById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/waitlists/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requestedSeats").value(2));
    }


    @Test
    void getWaitlistByEvent_shouldReturnOk() throws Exception {

        WaitlistResponse response =
                WaitlistResponse.builder()
                        .id(1L)
                        .requestedSeats(2)
                        .status(WaitlistStatus.WAITING)
                        .build();

        when(waitlistService.getWaitlistByEvent(10L))
                .thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/waitlists/event/10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }


    @Test
    void getWaitlistByEventAndStatus_shouldReturnOk()
            throws Exception {

        WaitlistResponse response =
                WaitlistResponse.builder()
                        .id(1L)
                        .status(WaitlistStatus.WAITING)
                        .build();

        when(
                waitlistService.getWaitlistByEventAndStatus(
                        10L,
                        WaitlistStatus.WAITING
                )
        ).thenReturn(List.of(response));

        mockMvc.perform(
                        get(
                                "/api/waitlists/event/10/status/WAITING"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }


    @Test
    void markAsNotified_shouldReturnOk() throws Exception {

        WaitlistResponse response =
                WaitlistResponse.builder()
                        .id(1L)
                        .status(WaitlistStatus.NOTIFIED)
                        .build();

        when(waitlistService.markAsNotified(1L))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/waitlists/1/notify")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NOTIFIED"));
    }


    @Test
    void markAsConverted_shouldReturnOk() throws Exception {

        WaitlistResponse response =
                WaitlistResponse.builder()
                        .id(1L)
                        .status(WaitlistStatus.CONVERTED)
                        .build();

        when(waitlistService.markAsConverted(1L))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/waitlists/1/convert")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONVERTED"));
    }


    @Test
    void cancelWaitlist_shouldReturnOk() throws Exception {

        WaitlistResponse response =
                WaitlistResponse.builder()
                        .id(1L)
                        .status(WaitlistStatus.CANCELLED)
                        .build();

        when(waitlistService.cancelWaitlist(1L))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/waitlists/1/cancel")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}