package com.lhind.event_booking_api.security;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;
import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.NotificationStatus;
import com.lhind.event_booking_api.entity.PaymentMethod;
import com.lhind.event_booking_api.entity.PaymentStatus;
import com.lhind.event_booking_api.entity.WaitlistStatus;
import com.lhind.event_booking_api.service.*;

import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private VenueService venueService;
    @MockitoBean
    private ReviewService reviewService;
    @MockitoBean
    private PaymentService paymentService;
    @MockitoBean
    private WaitlistService waitlistService;
    @MockitoBean
    private NotificationService notificationService;


    @Test
    void getEvents_shouldBePublic() throws Exception {

        when(eventService.getAllEvents())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/events")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void getVenues_shouldBePublic() throws Exception {

        when(venueService.getAllVenues())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/venues")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void getCurrentUser_shouldRejectAnonymousUser()
            throws Exception {

        mockMvc.perform(
                        get("/api/users/me")
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    @Test
    void createEvent_shouldRejectUserRole()
            throws Exception {

        mockMvc.perform(
                        post("/api/events")
                                .with(
                                        user("user@test.com")
                                                .roles("USER")
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void createEvent_shouldPassSecurityForOrganizer()
            throws Exception {

        mockMvc.perform(
                        post("/api/events")
                                .with(
                                        user("organizer@test.com")
                                                .roles("ORGANIZER")
                                )
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void createEvent_shouldPassSecurityForAdmin()
            throws Exception {

        mockMvc.perform(
                        post("/api/events")
                                .with(
                                        user("admin@test.com")
                                                .roles("ADMIN")
                                )
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void createVenue_shouldRejectUser()
            throws Exception {

        mockMvc.perform(
                        post("/api/venues")
                                .with(
                                        user("user@test.com")
                                                .roles("USER")
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void createVenue_shouldRejectOrganizer()
            throws Exception {

        mockMvc.perform(
                        post("/api/venues")
                                .with(
                                        user("organizer@test.com")
                                                .roles("ORGANIZER")
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void createVenue_shouldPassSecurityForAdmin()
            throws Exception {

        mockMvc.perform(
                        post("/api/venues")
                                .with(
                                        user("admin@test.com")
                                                .roles("ADMIN")
                                )
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void createCategory_shouldRejectUser() throws Exception {

        mockMvc.perform(
                        post("/api/categories")
                                .with(
                                        user("user@test.com")
                                                .roles("USER")
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void createCategory_shouldPassSecurityForAdmin() throws Exception {

        mockMvc.perform(
                        post("/api/categories")
                                .with(
                                        user("admin@test.com")
                                                .roles("ADMIN")
                                )
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void createSeat_shouldRejectOrganizer() throws Exception {

        mockMvc.perform(
                        post("/api/seats")
                                .with(
                                        user("organizer@test.com")
                                                .roles("ORGANIZER")
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void createSeat_shouldPassSecurityForAdmin() throws Exception {

        mockMvc.perform(
                        post("/api/seats")
                                .with(
                                        user("admin@test.com")
                                                .roles("ADMIN")
                                )
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void createEventSeat_shouldRejectUser() throws Exception {

        mockMvc.perform(
                        post("/api/event-seats/event/1")
                                .with(
                                        user("user@test.com")
                                                .roles("USER")
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void createEventSeat_shouldPassSecurityForOrganizer() throws Exception {

        mockMvc.perform(
                        post("/api/event-seats/event/1")
                                .with(
                                        user("organizer@test.com")
                                                .roles("ORGANIZER")
                                )
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void createBooking_shouldRejectAnonymousUser() throws Exception {

        mockMvc.perform(
                        post("/api/bookings")
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    @Test
    void createBooking_shouldPassSecurityForUser() throws Exception {

        mockMvc.perform(
                        post("/api/bookings")
                                .with(
                                        user("user@test.com")
                                                .roles("USER")
                                )
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void confirmBooking_shouldRejectUserRole() throws Exception {

        mockMvc.perform(
                        patch("/api/bookings/1/confirm")
                                .with(
                                        user("user@test.com")
                                                .roles("USER")
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void getReviewsByEvent_shouldBePublic() throws Exception {

        when(reviewService.getReviewsByEvent(1L))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/reviews/event/1")
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void createReview_shouldRejectAnonymousUser() throws Exception {

        mockMvc.perform(
                        post("/api/reviews")
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().is4xxClientError()
                );
    }

    @Test
    void createReview_shouldPassSecurityForUser() throws Exception {

        mockMvc.perform(
                        post("/api/reviews")
                                .with(
                                        user("user@test.com")
                                                .roles("USER")
                                )
                                .contentType("application/json")
                                .content("{}")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    // --------------------------------
// PAYMENTS
// --------------------------------

    @Test
    void createPayment_withoutAuthentication_shouldBeForbidden()
            throws Exception {

        mockMvc.perform(
                        post("/api/payments/booking/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "paymentMethod": "CARD"
                            }
                            """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void createPayment_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(paymentService.createPayment(
                eq(1L),
                any(PaymentRequest.class)
        )).thenReturn(
                PaymentResponse.builder()
                        .id(1L)
                        .amount(new BigDecimal("25.00"))
                        .paymentMethod(PaymentMethod.CARD)
                        .paymentStatus(PaymentStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        post("/api/payments/booking/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "paymentMethod": "CARD"
                            }
                            """)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void getPayment_withoutAuthentication_shouldBeForbidden()
            throws Exception {

        mockMvc.perform(
                        get("/api/payments/1")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void getPayment_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(paymentService.getPaymentById(1L))
                .thenReturn(
                        PaymentResponse.builder()
                                .id(1L)
                                .amount(new BigDecimal("25.00"))
                                .paymentMethod(PaymentMethod.CARD)
                                .paymentStatus(PaymentStatus.PENDING)
                                .build()
                );

        mockMvc.perform(
                        get("/api/payments/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void completePayment_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(paymentService.completePayment(1L))
                .thenReturn(
                        PaymentResponse.builder()
                                .id(1L)
                                .paymentStatus(PaymentStatus.COMPLETED)
                                .build()
                );

        mockMvc.perform(
                        patch("/api/payments/1/complete")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "admin@test.com",
            roles = "ADMIN"
    )
    void refundPayment_adminShouldBeAllowed()
            throws Exception {

        when(paymentService.refundPayment(1L))
                .thenReturn(
                        PaymentResponse.builder()
                                .id(1L)
                                .paymentStatus(PaymentStatus.REFUNDED)
                                .build()
                );

        mockMvc.perform(
                        patch("/api/payments/1/refund")
                )
                .andExpect(status().isOk());
    }

    @Test
    void joinWaitlist_withoutAuthentication_shouldBeForbidden()
            throws Exception {

        mockMvc.perform(
                        post("/api/waitlists/event/10")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "requestedSeats": 2
                            }
                            """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void joinWaitlist_authenticatedUser_shouldBeAllowed()
            throws Exception {

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
                                .content("""
                            {
                              "requestedSeats": 2
                            }
                            """)
                )
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void getMyWaitlists_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(waitlistService.getMyWaitlists())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/waitlists/my")
                )
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void getWaitlistById_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(waitlistService.getWaitlistById(1L))
                .thenReturn(
                        WaitlistResponse.builder()
                                .id(1L)
                                .status(WaitlistStatus.WAITING)
                                .build()
                );

        mockMvc.perform(
                        get("/api/waitlists/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void getEventWaitlist_userRole_shouldBeForbidden()
            throws Exception {

        mockMvc.perform(
                        get("/api/waitlists/event/10")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "organizer@test.com",
            roles = "ORGANIZER"
    )
    void getEventWaitlist_organizerShouldBeAllowed()
            throws Exception {

        when(waitlistService.getWaitlistByEvent(10L))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/waitlists/event/10")
                )
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(
            username = "organizer@test.com",
            roles = "ORGANIZER"
    )
    void getEventWaitlistByStatus_organizerShouldBeAllowed()
            throws Exception {

        when(
                waitlistService.getWaitlistByEventAndStatus(
                        10L,
                        WaitlistStatus.WAITING
                )
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/waitlists/event/10/status/WAITING")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void notifyWaitlist_userRole_shouldBeForbidden()
            throws Exception {

        mockMvc.perform(
                        patch("/api/waitlists/1/notify")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "organizer@test.com",
            roles = "ORGANIZER"
    )
    void notifyWaitlist_organizerShouldBeAllowed()
            throws Exception {

        when(waitlistService.markAsNotified(1L))
                .thenReturn(
                        WaitlistResponse.builder()
                                .id(1L)
                                .status(WaitlistStatus.NOTIFIED)
                                .build()
                );

        mockMvc.perform(
                        patch("/api/waitlists/1/notify")
                )
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(
            username = "admin@test.com",
            roles = "ADMIN"
    )
    void convertWaitlist_adminShouldBeAllowed()
            throws Exception {

        when(waitlistService.markAsConverted(1L))
                .thenReturn(
                        WaitlistResponse.builder()
                                .id(1L)
                                .status(WaitlistStatus.CONVERTED)
                                .build()
                );

        mockMvc.perform(
                        patch("/api/waitlists/1/convert")
                )
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void cancelWaitlist_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(waitlistService.cancelWaitlist(1L))
                .thenReturn(
                        WaitlistResponse.builder()
                                .id(1L)
                                .status(WaitlistStatus.CANCELLED)
                                .build()
                );

        mockMvc.perform(
                        patch("/api/waitlists/1/cancel")
                )
                .andExpect(status().isOk());
    }

    @Test
    void getMyNotifications_withoutAuthentication_shouldBeForbidden()
            throws Exception {

        mockMvc.perform(
                        get("/api/notifications/me")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void getMyNotifications_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(notificationService.getMyNotifications())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/notifications/me")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void getMyUnreadNotifications_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(notificationService.getMyUnreadNotifications())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/notifications/me/unread")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void countMyUnreadNotifications_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(notificationService.countMyUnreadNotifications())
                .thenReturn(2L);

        mockMvc.perform(
                        get("/api/notifications/me/unread/count")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void getNotificationById_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(notificationService.getNotificationById(1L))
                .thenReturn(
                        NotificationResponse.builder()
                                .id(1L)
                                .notificationStatus(NotificationStatus.UNREAD)
                                .build()
                );

        mockMvc.perform(
                        get("/api/notifications/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void markNotificationAsRead_authenticatedUser_shouldBeAllowed()
            throws Exception {

        when(notificationService.markAsRead(1L))
                .thenReturn(
                        NotificationResponse.builder()
                                .id(1L)
                                .notificationStatus(NotificationStatus.READ)
                                .build()
                );

        mockMvc.perform(
                        patch("/api/notifications/1/read")
                )
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "user@test.com",
            roles = "USER"
    )
    void markAllNotificationsAsRead_authenticatedUser_shouldBeAllowed()
            throws Exception {

        mockMvc.perform(
                        patch("/api/notifications/me/read-all")
                )
                .andExpect(status().isNoContent());
    }
}