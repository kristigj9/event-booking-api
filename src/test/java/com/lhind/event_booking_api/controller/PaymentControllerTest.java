package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.booking.BookingResponseShort;
import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;
import com.lhind.event_booking_api.entity.BookingStatus;
import com.lhind.event_booking_api.entity.PaymentMethod;
import com.lhind.event_booking_api.entity.PaymentStatus;
import com.lhind.event_booking_api.security.CustomUserDetailsService;
import com.lhind.event_booking_api.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.lhind.event_booking_api.security.JwtService;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)

class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockitoBean
    private PaymentService paymentService;

    @Test
    void createPayment_shouldReturnCreated() throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setPaymentMethod(PaymentMethod.CARD);

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .booking(
                        BookingResponseShort.builder()
                                .id(1L)
                                .status(BookingStatus.PENDING)
                                .build()
                )
                .build();

        when(paymentService.createPayment(
                eq(1L),
                any(PaymentRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        post("/api/payments/booking/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(25.00))
                .andExpect(jsonPath("$.paymentMethod").value("CARD"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.booking.id").value(1));
    }

    @Test
    void getPaymentById_shouldReturnOk() throws Exception {

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentService.getPaymentById(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/payments/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(25.00))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    void getPaymentByBookingId_shouldReturnOk() throws Exception {

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentService.getPaymentByBookingId(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/payments/booking/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    void completePayment_shouldReturnOk() throws Exception {

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.COMPLETED)
                .transactionId("TXN-TEST-001")
                .build();

        when(paymentService.completePayment(1L))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/payments/1/complete")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paymentStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.transactionId").value("TXN-TEST-001"));
    }

    @Test
    void refundPayment_shouldReturnOk() throws Exception {

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.REFUNDED)
                .build();

        when(paymentService.refundPayment(1L))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/payments/1/refund")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paymentStatus").value("REFUNDED"));
    }
}