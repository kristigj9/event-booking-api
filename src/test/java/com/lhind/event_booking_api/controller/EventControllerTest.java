package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lhind.event_booking_api.dto.event.EventRequest;
import com.lhind.event_booking_api.dto.event.EventResponse;
import com.lhind.event_booking_api.dto.event.EventUpdateRequest;
import com.lhind.event_booking_api.dto.reference.CategoryReferenceRequest;
import com.lhind.event_booking_api.dto.reference.VenueReferenceRequest;
import com.lhind.event_booking_api.entity.EventStatus;
import com.lhind.event_booking_api.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    private EventService eventService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private EventRequest eventRequest;

    private EventUpdateRequest eventUpdateRequest;

    private EventResponse eventResponse;

    @BeforeEach
    void setUp() {

        EventController eventController =
                new EventController(eventService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .build();

        objectMapper = new ObjectMapper();

        objectMapper.registerModule(
                new JavaTimeModule()
        );

        VenueReferenceRequest venue =
                new VenueReferenceRequest();

        venue.setVenueName("Tirana Arena");
        venue.setVenueCity("Tirana");

        CategoryReferenceRequest category =
                new CategoryReferenceRequest();

        category.setNameCategory("Music");

        eventRequest = new EventRequest();

        eventRequest.setEventName(
                "Music Event"
        );

        eventRequest.setEventDescription(
                "Test event"
        );

        eventRequest.setEventStartDateTime(
                LocalDateTime.now()
                        .plusDays(5)
        );

        eventRequest.setEventEndDateTime(
                LocalDateTime.now()
                        .plusDays(5)
                        .plusHours(3)
        );

        eventRequest.setEventTotalSeats(
                50
        );

        eventRequest.setEventStatus(
                EventStatus.PUBLISHED
        );

        eventRequest.setVenue(
                venue
        );

        eventRequest.setCategories(
                List.of(category)
        );

        eventUpdateRequest =
                new EventUpdateRequest();

        eventUpdateRequest.setEventName(
                "Updated Music Event"
        );

        eventUpdateRequest.setEventDescription(
                "Updated event"
        );

        eventUpdateRequest.setEventStartDateTime(
                LocalDateTime.now()
                        .plusDays(6)
        );

        eventUpdateRequest.setEventEndDateTime(
                LocalDateTime.now()
                        .plusDays(6)
                        .plusHours(3)
        );

        eventUpdateRequest.setEventTotalSeats(
                60
        );

        eventUpdateRequest.setEventStatus(
                EventStatus.PUBLISHED
        );

        eventUpdateRequest.setVenue(
                venue
        );

        eventUpdateRequest.setCategories(
                List.of(category)
        );

        eventResponse =
                EventResponse.builder()
                        .id(1L)
                        .eventName(
                                "Music Event"
                        )
                        .eventDescription(
                                "Test event"
                        )
                        .eventStartDateTime(
                                eventRequest
                                        .getEventStartDateTime()
                        )
                        .eventEndDateTime(
                                eventRequest
                                        .getEventEndDateTime()
                        )
                        .eventTotalSeats(
                                50
                        )
                        .eventAvailableSeats(
                                50
                        )
                        .eventStatus(
                                EventStatus.PUBLISHED
                        )
                        .build();
    }

    @Test
    void createEvent_shouldReturnCreated()
            throws Exception {

        when(eventService.createEvent(
                any(EventRequest.class)
        )).thenReturn(eventResponse);

        mockMvc.perform(
                        post("/api/events")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        eventRequest
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
                        jsonPath("$.eventName")
                                .value(
                                        "Music Event"
                                )
                )
                .andExpect(
                        jsonPath("$.eventStatus")
                                .value(
                                        "PUBLISHED"
                                )
                );

        verify(eventService, times(1))
                .createEvent(
                        any(EventRequest.class)
                );
    }

    @Test
    void getAllEvents_shouldReturnOk()
            throws Exception {

        when(eventService.getAllEvents())
                .thenReturn(
                        List.of(eventResponse)
                );

        mockMvc.perform(
                        get("/api/events")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].eventName")
                                .value(
                                        "Music Event"
                                )
                );

        verify(eventService, times(1))
                .getAllEvents();
    }

    @Test
    void getEventById_shouldReturnOk()
            throws Exception {

        when(eventService.getEventById(1L))
                .thenReturn(
                        eventResponse
                );

        mockMvc.perform(
                        get("/api/events/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.eventStatus")
                                .value(
                                        "PUBLISHED"
                                )
                );

        verify(eventService, times(1))
                .getEventById(1L);
    }

    @Test
    void getEventsByOrganizer_shouldReturnOk()
            throws Exception {

        when(eventService
                .getEventsByOrganizer(1L))
                .thenReturn(
                        List.of(eventResponse)
                );

        mockMvc.perform(
                        get(
                                "/api/events/organizer/1"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                );

        verify(eventService, times(1))
                .getEventsByOrganizer(1L);
    }

    @Test
    void getEventsByStatus_shouldReturnOk()
            throws Exception {

        when(eventService.getEventsByStatus(
                EventStatus.PUBLISHED
        )).thenReturn(
                List.of(eventResponse)
        );

        mockMvc.perform(
                        get(
                                "/api/events/status/PUBLISHED"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$[0].eventStatus"
                        )
                                .value(
                                        "PUBLISHED"
                                )
                );

        verify(eventService, times(1))
                .getEventsByStatus(
                        EventStatus.PUBLISHED
                );
    }

    @Test
    void getEventsByCategory_shouldReturnOk()
            throws Exception {

        when(eventService
                .getEventsByCategory(1L))
                .thenReturn(
                        List.of(eventResponse)
                );

        mockMvc.perform(
                        get(
                                "/api/events/category/1"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                );

        verify(eventService, times(1))
                .getEventsByCategory(1L);
    }

    @Test
    void getEventsByVenue_shouldReturnOk()
            throws Exception {

        when(eventService
                .getEventsByVenue(1L))
                .thenReturn(
                        List.of(eventResponse)
                );

        mockMvc.perform(
                        get(
                                "/api/events/venue/1"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                );

        verify(eventService, times(1))
                .getEventsByVenue(1L);
    }

    @Test
    void updateEvent_shouldReturnOk()
            throws Exception {

        EventResponse updatedResponse =
                EventResponse.builder()
                        .id(1L)
                        .eventName(
                                "Updated Music Event"
                        )
                        .eventDescription(
                                "Updated event"
                        )
                        .eventStartDateTime(
                                eventUpdateRequest
                                        .getEventStartDateTime()
                        )
                        .eventEndDateTime(
                                eventUpdateRequest
                                        .getEventEndDateTime()
                        )
                        .eventTotalSeats(
                                60
                        )
                        .eventAvailableSeats(
                                60
                        )
                        .eventStatus(
                                EventStatus.PUBLISHED
                        )
                        .build();

        when(eventService.updateEvent(
                eq(1L),
                any(EventUpdateRequest.class)
        )).thenReturn(
                updatedResponse
        );

        mockMvc.perform(
                        put("/api/events/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        eventUpdateRequest
                                                )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.eventName")
                                .value(
                                        "Updated Music Event"
                                )
                )
                .andExpect(
                        jsonPath(
                                "$.eventTotalSeats"
                        )
                                .value(60)
                )
                .andExpect(
                        jsonPath("$.eventStatus")
                                .value(
                                        "PUBLISHED"
                                )
                );

        verify(eventService, times(1))
                .updateEvent(
                        eq(1L),
                        any(EventUpdateRequest.class)
                );
    }

    @Test
    void deleteEvent_shouldReturnNoContent()
            throws Exception {

        mockMvc.perform(
                        delete("/api/events/1")
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(eventService, times(1))
                .deleteEvent(1L);
    }

    @Test
    void createEvent_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        EventRequest invalidRequest =
                new EventRequest();

        mockMvc.perform(
                        post("/api/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(eventService, never())
                .createEvent(
                        any(EventRequest.class)
                );
    }

    @Test
    void updateEvent_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        EventUpdateRequest invalidRequest =
                new EventUpdateRequest();

        mockMvc.perform(
                        put("/api/events/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(eventService, never())
                .updateEvent(
                        eq(1L),
                        any(EventUpdateRequest.class)
                );
    }
}