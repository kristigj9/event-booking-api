package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.event.EventRequest;
import com.lhind.event_booking_api.dto.event.EventResponse;
import com.lhind.event_booking_api.dto.reference.CategoryReferenceRequest;
import com.lhind.event_booking_api.entity.Category;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.EventStatus;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.entity.Venue;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.EventMapper;
import com.lhind.event_booking_api.repository.CategoryRepository;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.UserRepository;
import com.lhind.event_booking_api.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    public EventServiceImpl(
            EventRepository eventRepository,
            UserRepository userRepository,
            VenueRepository venueRepository,
            CategoryRepository categoryRepository,
            EventMapper eventMapper
    ) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
    }

    // CREATE EVENT
    @Override
    @Transactional
    public EventResponse createEvent(
            EventRequest request,
            Long organizerId
    ) {

        User organizer = findOrganizer(organizerId);

        Venue venue = findVenue(request);

        List<Category> categories =
                findCategories(request);

        validateEventDates(request);

        validateVenueCapacity(request, venue);

        Event event = eventMapper.toEntity(
                request,
                organizer,
                venue,
                categories
        );

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toResponse(savedEvent);
    }

    // GET EVENT BY ID
    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {

        Event event = findEvent(id);

        return eventMapper.toResponse(event);
    }

    // GET ALL EVENTS
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {

        return eventMapper.toResponseList(
                eventRepository.findAll()
        );
    }

    // GET EVENTS BY ORGANIZER
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByOrganizer(
            Long organizerId
    ) {

        return eventMapper.toResponseList(
                eventRepository.findByOrganizerId(organizerId)
        );
    }

    // GET EVENTS BY STATUS
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByStatus(
            EventStatus status
    ) {

        return eventMapper.toResponseList(
                eventRepository.findByEventStatus(status)
        );
    }

    // GET EVENTS BY CATEGORY
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByCategory(
            Long categoryId
    ) {

        return eventMapper.toResponseList(
                eventRepository.findByCategoriesId(categoryId)
        );
    }

    // GET EVENTS BY VENUE
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByVenue(
            Long venueId
    ) {

        return eventMapper.toResponseList(
                eventRepository.findByVenueId(venueId)
        );
    }

    // UPDATE EVENT
    @Override
    @Transactional
    public EventResponse updateEvent(
            Long eventId,
            Long organizerId,
            EventRequest request
    ) {

        Event event = findEvent(eventId);

        validateOwnership(event, organizerId);

        Venue venue = findVenue(request);

        List<Category> categories =
                findCategories(request);

        validateEventDates(request);

        validateVenueCapacity(request, venue);

        /*
         * Ruajmë diferencën midis totalSeats dhe
         * availableSeats për të ditur sa vende janë
         * aktualisht të zëna.
         */
        int bookedSeats =
                event.getEventTotalSeats()
                        - event.getEventAvailableSeats();

        if (request.getEventTotalSeats() < bookedSeats) {
            throw new InvalidOperationException(
                    "Total seats cannot be less than already booked seats"
            );
        }

        eventMapper.updateEntity(
                request,
                event,
                venue,
                categories
        );

        /*
         * Available seats llogariten përsëri duke
         * respektuar vendet tashmë të rezervuara.
         */
        event.setEventAvailableSeats(
                request.getEventTotalSeats() - bookedSeats
        );

        Event updatedEvent =
                eventRepository.save(event);

        return eventMapper.toResponse(updatedEvent);
    }

    // DELETE EVENT
    @Override
    @Transactional
    public void deleteEvent(
            Long eventId,
            Long organizerId
    ) {

        Event event = findEvent(eventId);

        validateOwnership(event, organizerId);

        eventRepository.delete(event);
    }

    // -----------------------------
    // PRIVATE HELPER METHODS
    // -----------------------------

    private Event findEvent(Long eventId) {

        return eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: "
                                        + eventId
                        )
                );
    }

    private User findOrganizer(Long organizerId) {

        return userRepository.findById(organizerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organizer not found with id: "
                                        + organizerId
                        )
                );
    }

    private Venue findVenue(EventRequest request) {

        return venueRepository
                .findByVenueNameAndVenueCity(
                        request.getVenue().getVenueName(),
                        request.getVenue().getVenueCity()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Venue not found: "
                                        + request.getVenue().getVenueName()
                        )
                );
    }

    private List<Category> findCategories(
            EventRequest request
    ) {

        return request.getCategories()
                .stream()
                .map(CategoryReferenceRequest::getNameCategory)
                .map(name ->
                        categoryRepository
                                .findByNameCategory(name)
                                .orElseThrow(() ->
                                        new ResourceNotFoundException(
                                                "Category not found: "
                                                        + name
                                        )
                                )
                )
                .toList();
    }

    private void validateEventDates(
            EventRequest request
    ) {

        if (!request.getEventEndDateTime()
                .isAfter(request.getEventStartDateTime())) {

            throw new InvalidOperationException(
                    "Event end date must be after start date"
            );
        }
    }

    private void validateVenueCapacity(
            EventRequest request,
            Venue venue
    ) {

        if (request.getEventTotalSeats()
                > venue.getVenueCapacity()) {

            throw new InvalidOperationException(
                    "Event total seats cannot exceed venue capacity"
            );
        }
    }

    private void validateOwnership(
            Event event,
            Long organizerId
    ) {

        if (!event.getOrganizer()
                .getId()
                .equals(organizerId)) {

            throw new InvalidOperationException(
                    "You are not allowed to modify this event"
            );
        }
    }
}