package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.entity.NotificationStatus;
import com.lhind.event_booking_api.entity.NotificationType;
import com.lhind.event_booking_api.security.CustomUserDetailsService;
import com.lhind.event_booking_api.security.JwtService;
import com.lhind.event_booking_api.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    void getMyNotifications_shouldReturnOk() throws Exception {

        NotificationResponse response =
                NotificationResponse.builder()
                        .id(1L)
                        .notificationType(
                                NotificationType.BOOKING_CONFIRMED
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Booking confirmed")
                        .build();

        when(notificationService.getMyNotifications())
                .thenReturn(
                        List.of(response)
                );

        mockMvc.perform(
                        get("/api/notifications/me")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].notificationType")
                                .value("BOOKING_CONFIRMED")
                )
                .andExpect(
                        jsonPath("$[0].notificationStatus")
                                .value("UNREAD")
                );
    }


    @Test
    void getMyUnreadNotifications_shouldReturnOk()
            throws Exception {

        NotificationResponse response =
                NotificationResponse.builder()
                        .id(1L)
                        .notificationType(
                                NotificationType.PAYMENT_COMPLETED
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Payment completed")
                        .build();

        when(
                notificationService
                        .getMyUnreadNotifications()
        ).thenReturn(
                List.of(response)
        );

        mockMvc.perform(
                        get("/api/notifications/me/unread")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].notificationStatus")
                                .value("UNREAD")
                );
    }


    @Test
    void countMyUnreadNotifications_shouldReturnOk()
            throws Exception {

        when(
                notificationService
                        .countMyUnreadNotifications()
        ).thenReturn(3L);

        mockMvc.perform(
                        get(
                                "/api/notifications/me/unread/count"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        content().string("3")
                );
    }


    @Test
    void getNotificationById_shouldReturnOk()
            throws Exception {

        NotificationResponse response =
                NotificationResponse.builder()
                        .id(1L)
                        .notificationType(
                                NotificationType.WAITLIST_AVAILABLE
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Seats are available")
                        .build();

        when(
                notificationService
                        .getNotificationById(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/notifications/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.notificationType")
                                .value("WAITLIST_AVAILABLE")
                );
    }


    @Test
    void markAsRead_shouldReturnOk()
            throws Exception {

        NotificationResponse response =
                NotificationResponse.builder()
                        .id(1L)
                        .notificationType(
                                NotificationType.BOOKING_CONFIRMED
                        )
                        .notificationStatus(
                                NotificationStatus.READ
                        )
                        .message("Booking confirmed")
                        .build();

        when(
                notificationService
                        .markAsRead(1L)
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/notifications/1/read"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.notificationStatus")
                                .value("READ")
                );
    }


    @Test
    void markAllAsRead_shouldReturnNoContent()
            throws Exception {

        mockMvc.perform(
                        patch(
                                "/api/notifications/me/read-all"
                        )
                )
                .andExpect(
                        status().isNoContent()
                );
    }
}