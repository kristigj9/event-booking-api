package com.lhind.event_booking_api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.controller.PaymentController;
import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;
import com.lhind.event_booking_api.entity.PaymentMethod;
import com.lhind.event_booking_api.entity.PaymentStatus;
import com.lhind.event_booking_api.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
class PaymentSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    // --------------------------------
    // WITHOUT AUTHENTICATION
    // --------------------------------

    @Test
    void createPayment_withoutAuthentication_shouldBeForbidden()
            throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setPaymentMethod(PaymentMethod.CARD);

        mockMvc.perform(
                        post("/api/payments/booking/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }


    @Test
    void getPaymentById_withoutAuthentication_shouldBeForbidden()
            throws Exception {

        mockMvc.perform(
                        get("/api/payments/1")
                )
                .andExpect(
                        status().isForbidden()
                );
    }


    @Test
    void completePayment_withoutAuthentication_shouldBeForbidden()
            throws Exception {

        mockMvc.perform(
                        patch("/api/payments/1/complete")
                )
                .andExpect(
                        status().isForbidden()
                );
    }


    // --------------------------------
    // AUTHENTICATED USER
    // --------------------------------

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void createPayment_authenticatedUser_shouldBeAllowed()
            throws Exception {

        PaymentRequest request = new PaymentRequest();
        request.setPaymentMethod(PaymentMethod.CARD);

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(
                                new BigDecimal("25.00")
                        )
                        .paymentMethod(
                                PaymentMethod.CARD
                        )
                        .paymentStatus(
                                PaymentStatus.PENDING
                        )
                        .build();

        when(
                paymentService.createPayment(
                        eq(1L),
                        any(PaymentRequest.class)
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/payments/booking/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id").value(1)
                )
                .andExpect(
                        jsonPath("$.paymentStatus")
                                .value("PENDING")
                );
    }


    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void getPaymentById_authenticatedUser_shouldBeAllowed()
            throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(
                                new BigDecimal("25.00")
                        )
                        .paymentMethod(
                                PaymentMethod.CARD
                        )
                        .paymentStatus(
                                PaymentStatus.PENDING
                        )
                        .build();

        when(
                paymentService.getPaymentById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/payments/1")
                )
                .andExpect(
                        status().isOk()
                );
    }


    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void completePayment_authenticatedUser_shouldBeAllowed()
            throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(
                                new BigDecimal("25.00")
                        )
                        .paymentMethod(
                                PaymentMethod.CARD
                        )
                        .paymentStatus(
                                PaymentStatus.COMPLETED
                        )
                        .build();

        when(
                paymentService.completePayment(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/payments/1/complete"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.paymentStatus")
                                .value("COMPLETED")
                );
    }


    // --------------------------------
    // ADMIN
    // --------------------------------

    @Test
    @WithMockUser(
            username = "admin@test.com",
            roles = "ADMIN"
    )
    void refundPayment_adminShouldBeAllowed()
            throws Exception {

        PaymentResponse response =
                PaymentResponse.builder()
                        .id(1L)
                        .amount(
                                new BigDecimal("25.00")
                        )
                        .paymentMethod(
                                PaymentMethod.CARD
                        )
                        .paymentStatus(
                                PaymentStatus.REFUNDED
                        )
                        .build();

        when(
                paymentService.refundPayment(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/payments/1/refund"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.paymentStatus")
                                .value("REFUNDED")
                );
    }
}