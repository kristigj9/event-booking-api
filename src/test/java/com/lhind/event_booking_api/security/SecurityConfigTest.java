package com.lhind.event_booking_api.security;

import com.lhind.event_booking_api.service.EventService;
import com.lhind.event_booking_api.service.ReviewService;
import com.lhind.event_booking_api.service.VenueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
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
}