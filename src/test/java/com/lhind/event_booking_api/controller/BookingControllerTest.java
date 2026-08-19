package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.booking.BookingRequest;
import com.lhind.event_booking_api.dto.booking.BookingResponse;
import com.lhind.event_booking_api.dto.reference.SeatSelectionRequest;
import com.lhind.event_booking_api.entity.BookingStatus;
import com.lhind.event_booking_api.service.BookingService;
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
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private BookingRequest bookingRequest;

    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {

        BookingController bookingController =
                new BookingController(bookingService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        objectMapper = new ObjectMapper();

        SeatSelectionRequest seat =
                new SeatSelectionRequest();

        seat.setRowNumber("A");
        seat.setSeatNumber(1);

        bookingRequest =
                new BookingRequest();

        bookingRequest.setEventId(1L);
        bookingRequest.setSeats(
                List.of(seat)
        );

        bookingResponse =
                BookingResponse.builder()
                        .id(1L)
                        .bookingStatus(
                                BookingStatus.PENDING
                        )
                        .seatsBooked(1)
                        .build();
    }

    @Test
    void createBooking_shouldReturnCreated()
            throws Exception {

        when(bookingService.createBooking(
                any(BookingRequest.class)
        )).thenReturn(bookingResponse);

        mockMvc.perform(
                        post("/api/bookings")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                bookingRequest
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
                        jsonPath("$.bookingStatus")
                                .value("PENDING")
                )
                .andExpect(
                        jsonPath("$.seatsBooked")
                                .value(1)
                );

        verify(bookingService, times(1))
                .createBooking(
                        any(BookingRequest.class)
                );
    }

    @Test
    void getBookingById_shouldReturnOk()
            throws Exception {

        when(bookingService.getBookingById(1L))
                .thenReturn(bookingResponse);

        mockMvc.perform(
                        get("/api/bookings/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.bookingStatus")
                                .value("PENDING")
                );

        verify(bookingService, times(1))
                .getBookingById(1L);
    }

    @Test
    void getMyBookings_shouldReturnOk()
            throws Exception {

        when(bookingService.getMyBookings())
                .thenReturn(
                        List.of(bookingResponse)
                );

        mockMvc.perform(
                        get("/api/bookings/me")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].bookingStatus")
                                .value("PENDING")
                );

        verify(bookingService, times(1))
                .getMyBookings();
    }

    @Test
    void getMyBookingsByStatus_shouldReturnOk()
            throws Exception {

        when(bookingService
                .getMyBookingsByStatus(
                        BookingStatus.PENDING
                ))
                .thenReturn(
                        List.of(bookingResponse)
                );

        mockMvc.perform(
                        get(
                                "/api/bookings/me/status/PENDING"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].bookingStatus")
                                .value("PENDING")
                );

        verify(bookingService, times(1))
                .getMyBookingsByStatus(
                        BookingStatus.PENDING
                );
    }

    @Test
    void getBookingsByEvent_shouldReturnOk()
            throws Exception {

        when(bookingService
                .getBookingsByEvent(1L))
                .thenReturn(
                        List.of(bookingResponse)
                );

        mockMvc.perform(
                        get(
                                "/api/bookings/event/1"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                );

        verify(bookingService, times(1))
                .getBookingsByEvent(1L);
    }

    @Test
    void confirmBooking_shouldReturnOk()
            throws Exception {

        BookingResponse confirmedResponse =
                BookingResponse.builder()
                        .id(1L)
                        .bookingStatus(
                                BookingStatus.CONFIRMED
                        )
                        .seatsBooked(1)
                        .build();

        when(bookingService.confirmBooking(1L))
                .thenReturn(confirmedResponse);

        mockMvc.perform(
                        patch(
                                "/api/bookings/1/confirm"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.bookingStatus")
                                .value("CONFIRMED")
                );

        verify(bookingService, times(1))
                .confirmBooking(1L);
    }

    @Test
    void cancelBooking_shouldReturnOk()
            throws Exception {

        BookingResponse cancelledResponse =
                BookingResponse.builder()
                        .id(1L)
                        .bookingStatus(
                                BookingStatus.CANCELLED
                        )
                        .seatsBooked(1)
                        .build();

        when(bookingService.cancelBooking(1L))
                .thenReturn(cancelledResponse);

        mockMvc.perform(
                        patch(
                                "/api/bookings/1/cancel"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.bookingStatus")
                                .value("CANCELLED")
                );

        verify(bookingService, times(1))
                .cancelBooking(1L);
    }

    @Test
    void completeBooking_shouldReturnOk()
            throws Exception {

        BookingResponse completedResponse =
                BookingResponse.builder()
                        .id(1L)
                        .bookingStatus(
                                BookingStatus.COMPLETED
                        )
                        .seatsBooked(1)
                        .build();

        when(bookingService.completeBooking(1L))
                .thenReturn(completedResponse);

        mockMvc.perform(
                        patch(
                                "/api/bookings/1/complete"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.bookingStatus")
                                .value("COMPLETED")
                );

        verify(bookingService, times(1))
                .completeBooking(1L);
    }

    @Test
    void createBooking_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        BookingRequest invalidRequest =
                new BookingRequest();

        mockMvc.perform(
                        post("/api/bookings")
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

        verify(bookingService, never())
                .createBooking(
                        any(BookingRequest.class)
                );
    }
}