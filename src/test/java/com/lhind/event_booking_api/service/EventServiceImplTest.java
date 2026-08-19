package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.event.EventRequest;
import com.lhind.event_booking_api.dto.event.EventResponse;
import com.lhind.event_booking_api.dto.event.EventUpdateRequest;
import com.lhind.event_booking_api.dto.reference.CategoryReferenceRequest;
import com.lhind.event_booking_api.dto.reference.VenueReferenceRequest;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.EventMapper;
import com.lhind.event_booking_api.repository.CategoryRepository;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.VenueRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private EventServiceImpl eventService;

    private User organizer;
    private Venue venue;
    private Category category;
    private Event event;
    private EventRequest request;
    private EventResponse response;

    @BeforeEach
    void setUp() {

        organizer = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("Organizer")
                .email("organizer@test.com")
                .role(Role.ORGANIZER)
                .build();

        venue = Venue.builder()
                .id(1L)
                .venueName("Tirana Arena")
                .venueAddress("Tirana")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        category = Category.builder()
                .id(1L)
                .nameCategory("Music")
                .descriptionCategory("Music events")
                .build();

        VenueReferenceRequest venueRequest =
                new VenueReferenceRequest();

        venueRequest.setVenueName("Tirana Arena");
        venueRequest.setVenueCity("Tirana");

        CategoryReferenceRequest categoryRequest =
                new CategoryReferenceRequest();

        categoryRequest.setNameCategory("Music");

        request = new EventRequest();

        request.setEventName("Music Event");
        request.setEventDescription("Test event");
        request.setEventStartDateTime(
                LocalDateTime.now().plusDays(5)
        );
        request.setEventEndDateTime(
                LocalDateTime.now().plusDays(5).plusHours(3)
        );
        request.setEventTotalSeats(50);
        request.setEventStatus(EventStatus.PUBLISHED);
        request.setVenue(venueRequest);
        request.setCategories(
                List.of(categoryRequest)
        );

        event = Event.builder()
                .id(1L)
                .eventName("Music Event")
                .eventDescription("Test event")
                .eventStartDateTime(
                        request.getEventStartDateTime()
                )
                .eventEndDateTime(
                        request.getEventEndDateTime()
                )
                .eventTotalSeats(50)
                .eventAvailableSeats(50)
                .eventStatus(EventStatus.PUBLISHED)
                .organizer(organizer)
                .venue(venue)
                .categories(List.of(category))
                .build();

        response = EventResponse.builder()
                .id(1L)
                .eventName("Music Event")
                .eventStatus(EventStatus.PUBLISHED)
                .eventTotalSeats(50)
                .eventAvailableSeats(50)
                .build();
    }

    @Test
    void createEvent_shouldCreateEventSuccessfully() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(categoryRepository
                .findByNameCategory("Music"))
                .thenReturn(Optional.of(category));

        when(eventMapper.toEntity(
                request,
                organizer,
                venue,
                List.of(category)
        )).thenReturn(event);

        when(eventRepository.save(event))
                .thenReturn(event);

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse result =
                eventService.createEvent(request);

        assertNotNull(result);

        assertEquals(
                EventStatus.PUBLISHED,
                result.getEventStatus()
        );

        assertEquals(
                50,
                result.getEventTotalSeats()
        );

        verify(eventRepository, times(1))
                .save(event);
    }

    @Test
    void createEvent_shouldThrowExceptionWhenEndDateIsBeforeStartDate() {

        request.setEventEndDateTime(
                request.getEventStartDateTime()
                        .minusHours(1)
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(categoryRepository
                .findByNameCategory("Music"))
                .thenReturn(Optional.of(category));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventService.createEvent(request)
                );

        assertEquals(
                "Event end date must be after start date",
                exception.getMessage()
        );

        verify(eventRepository, never())
                .save(any(Event.class));
    }

    @Test
    void createEvent_shouldThrowExceptionWhenVenueCapacityIsExceeded() {

        request.setEventTotalSeats(150);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(categoryRepository
                .findByNameCategory("Music"))
                .thenReturn(Optional.of(category));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventService.createEvent(request)
                );

        assertEquals(
                "Event total seats cannot exceed venue capacity",
                exception.getMessage()
        );

        verify(eventRepository, never())
                .save(any(Event.class));
    }

//    UPDATE EVENT TEST
    //sipas ownership + capacity + lifecycle te statusit.


    @Test
    void updateEvent_shouldUpdateEventSuccessfully() {

        EventUpdateRequest updateRequest =
                new EventUpdateRequest();

        VenueReferenceRequest venueRequest =
                new VenueReferenceRequest();

        venueRequest.setVenueName("Tirana Arena");
        venueRequest.setVenueCity("Tirana");

        CategoryReferenceRequest categoryRequest =
                new CategoryReferenceRequest();

        categoryRequest.setNameCategory("Music");

        updateRequest.setEventName("Updated Music Event");
        updateRequest.setEventDescription("Updated description");
        updateRequest.setEventStartDateTime(
                LocalDateTime.now().plusDays(5)
        );
        updateRequest.setEventEndDateTime(
                LocalDateTime.now().plusDays(5).plusHours(3)
        );
        updateRequest.setEventTotalSeats(60);
        updateRequest.setEventStatus(EventStatus.PUBLISHED);
        updateRequest.setVenue(venueRequest);
        updateRequest.setCategories(
                List.of(categoryRequest)
        );

        event.setCategories(
                new ArrayList<>(List.of(category))
        );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(categoryRepository
                .findByNameCategory("Music"))
                .thenReturn(Optional.of(category));

        when(eventRepository.save(event))
                .thenReturn(event);

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse result =
                eventService.updateEvent(
                        1L,
                        updateRequest
                );

        assertNotNull(result);

        verify(eventMapper, times(1))
                .updateEntity(
                        eq(updateRequest),
                        eq(event),
                        eq(venue),
                        anyList()
                );

        verify(eventRepository, times(1))
                .save(event);
    }

    @Test
    void updateEvent_shouldThrowExceptionWhenOrganizerIsNotOwner() {

        User anotherOrganizer = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("Organizer")
                .email("other@test.com")
                .role(Role.ORGANIZER)
                .build();

        EventUpdateRequest updateRequest =
                new EventUpdateRequest();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherOrganizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventService.updateEvent(
                                1L,
                                updateRequest
                        )
                );

        assertEquals(
                "You are not allowed to modify this event",
                exception.getMessage()
        );

        verify(eventRepository, never())
                .save(any(Event.class));


    }
    //Tani lifecycle DRAFT -> PUBLISHED

    @Test
    void updateEvent_shouldAllowDraftToPublishedTransition() {

        event.setEventStatus(
                EventStatus.DRAFT
        );

        EventUpdateRequest updateRequest =
                createValidUpdateRequest(
                        EventStatus.PUBLISHED
                );

        event.setCategories(
                new ArrayList<>(List.of(category))
        );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(categoryRepository
                .findByNameCategory("Music"))
                .thenReturn(Optional.of(category));

        when(eventRepository.save(event))
                .thenReturn(event);

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse result =
                eventService.updateEvent(
                        1L,
                        updateRequest
                );

        assertNotNull(result);

        verify(eventRepository, times(1))
                .save(event);
    }

    //COMPLETED -> PUBLISHED bllokohet

    @Test
    void updateEvent_shouldThrowExceptionWhenCompletedEventStatusChanges() {

        event.setEventStatus(
                EventStatus.COMPLETED
        );

        EventUpdateRequest updateRequest =
                createValidUpdateRequest(
                        EventStatus.PUBLISHED
                );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(venueRepository
                .findByVenueNameAndVenueCity(
                        "Tirana Arena",
                        "Tirana"
                ))
                .thenReturn(Optional.of(venue));

        when(categoryRepository
                .findByNameCategory("Music"))
                .thenReturn(Optional.of(category));

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventService.updateEvent(
                                1L,
                                updateRequest
                        )
                );

        assertEquals(
                "Completed event status cannot be changed",
                exception.getMessage()
        );
    }

    private EventUpdateRequest createValidUpdateRequest(
            EventStatus status
    ) {

        VenueReferenceRequest venueRequest =
                new VenueReferenceRequest();

        venueRequest.setVenueName("Tirana Arena");
        venueRequest.setVenueCity("Tirana");

        CategoryReferenceRequest categoryRequest =
                new CategoryReferenceRequest();

        categoryRequest.setNameCategory("Music");

        EventUpdateRequest request =
                new EventUpdateRequest();

        request.setEventName("Updated Event");
        request.setEventDescription("Updated description");

        request.setEventStartDateTime(
                LocalDateTime.now().plusDays(2)
        );

        request.setEventEndDateTime(
                LocalDateTime.now().plusDays(2).plusHours(3)
        );

        request.setEventTotalSeats(50);
        request.setEventStatus(status);
        request.setVenue(venueRequest);
        request.setCategories(
                List.of(categoryRequest)
        );

        return request;
    }

//    DELETE EVENT
@Test
void deleteEvent_shouldDeleteEventSuccessfully() {

    when(eventRepository.findById(1L))
            .thenReturn(Optional.of(event));

    when(authenticatedUserService.getCurrentUser())
            .thenReturn(organizer);

    eventService.deleteEvent(1L);

    verify(eventRepository, times(1))
            .delete(event);
}
//ORGANIZER  nuk eshte OWNER

    @Test
    void deleteEvent_shouldThrowExceptionWhenOrganizerIsNotOwner() {

        User anotherOrganizer = User.builder()
                .id(99L)
                .firstName("Other")
                .lastName("Organizer")
                .email("other@test.com")
                .role(Role.ORGANIZER)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherOrganizer);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> eventService.deleteEvent(1L)
                );

        assertEquals(
                "You are not allowed to modify this event",
                exception.getMessage()
        );

        verify(eventRepository, never())
                .delete(any(Event.class));


    }

    //        GET EVENT BY ID

    @Test
    void getEventById_shouldReturnEventSuccessfully() {

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(eventMapper.toResponse(event))
                .thenReturn(response);

        EventResponse result =
                eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals(response, result);

        verify(eventRepository, times(1))

                .findById(1L);
    }

    //Event not found:

    @Test
    void getEventById_shouldThrowExceptionWhenEventNotFound() {

        when(eventRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> eventService.getEventById(99L)
                );

        assertEquals(
                "Event not found with id: 99",
                exception.getMessage()
        );
    }

//    GET ALL EVENTS

    @Test
    void getAllEvents_shouldReturnAllEvents() {

        List<Event> events =
                List.of(event);

        List<EventResponse> responses =
                List.of(response);

        when(eventRepository.findAll())
                .thenReturn(events);

        when(eventMapper.toResponseList(events))
                .thenReturn(responses);

        List<EventResponse> result =
                eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getEventsByOrganizer_shouldReturnEvents() {

        List<Event> events = List.of(event);
        List<EventResponse> responses = List.of(response);

        when(eventRepository.findByOrganizerId(1L))
                .thenReturn(events);

        when(eventMapper.toResponseList(events))
                .thenReturn(responses);

        List<EventResponse> result =
                eventService.getEventsByOrganizer(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getEventsByStatus_shouldReturnEvents() {

        List<Event> events = List.of(event);
        List<EventResponse> responses = List.of(response);

        when(eventRepository.findByEventStatus(
                EventStatus.PUBLISHED
        )).thenReturn(events);

        when(eventMapper.toResponseList(events))
                .thenReturn(responses);

        List<EventResponse> result =
                eventService.getEventsByStatus(
                        EventStatus.PUBLISHED
                );

        assertEquals(1, result.size());
    }

    @Test
    void getEventsByCategory_shouldReturnEvents() {

        List<Event> events = List.of(event);
        List<EventResponse> responses = List.of(response);

        when(eventRepository.findByCategoriesId(1L))
                .thenReturn(events);

        when(eventMapper.toResponseList(events))
                .thenReturn(responses);

        List<EventResponse> result =
                eventService.getEventsByCategory(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getEventsByVenue_shouldReturnEvents() {

        List<Event> events = List.of(event);
        List<EventResponse> responses = List.of(response);

        when(eventRepository.findByVenueId(1L))
                .thenReturn(events);

        when(eventMapper.toResponseList(events))
                .thenReturn(responses);

        List<EventResponse> result =
                eventService.getEventsByVenue(1L);

        assertEquals(1, result.size());
    }
}