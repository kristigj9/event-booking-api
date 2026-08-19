package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.event.EventRequest;
import com.lhind.event_booking_api.dto.event.EventResponse;
import com.lhind.event_booking_api.dto.event.EventUpdateRequest;
import com.lhind.event_booking_api.dto.reference.CategoryReferenceRequest;
import com.lhind.event_booking_api.entity.Category;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.EventStatus;
import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.entity.Venue;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.EventMapper;
import com.lhind.event_booking_api.repository.CategoryRepository;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.VenueRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger log =
            LogManager.getLogger(EventServiceImpl.class);

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final AuthenticatedUserService authenticatedUserService;

    public EventServiceImpl(
            EventRepository eventRepository,
            VenueRepository venueRepository,
            CategoryRepository categoryRepository,
            EventMapper eventMapper,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.authenticatedUserService = authenticatedUserService;
    }

    // CREATE EVENT
    @Override
    @Transactional
    public EventResponse createEvent(
            EventRequest request
    ) {

        User organizer =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Creating event '{}' by organizer id: {}",
                request.getEventName(),
                organizer.getId()
        );

        Venue venue =
                findVenue(
                        request.getVenue().getVenueName(),
                        request.getVenue().getVenueCity()
                );

        List<Category> categories =
                findCategories(request.getCategories());

        validateEventDates(
                request.getEventStartDateTime(),
                request.getEventEndDateTime()
        );

        validateVenueCapacity(
                request.getEventTotalSeats(),
                venue
        );

        Event event =
                eventMapper.toEntity(
                        request,
                        organizer,
                        venue,
                        categories
                );

        Event savedEvent =
                eventRepository.save(event);

        log.info(
                "Event created successfully with id: {}",
                savedEvent.getId()
        );

        return eventMapper.toResponse(savedEvent);
    }

    // GET EVENT BY ID
    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {

        log.debug(
                "Fetching event by id: {}",
                id
        );

        Event event =
                findEvent(id);

        return eventMapper.toResponse(event);
    }

    // GET ALL EVENTS
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents() {

        log.debug(
                "Fetching all events"
        );

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

        log.debug(
                "Fetching events for organizer id: {}",
                organizerId
        );

        return eventMapper.toResponseList(
                eventRepository.findByOrganizerId(
                        organizerId
                )
        );
    }

    // GET EVENTS BY STATUS
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByStatus(
            EventStatus status
    ) {

        log.debug(
                "Fetching events by status: {}",
                status
        );

        return eventMapper.toResponseList(
                eventRepository.findByEventStatus(
                        status
                )
        );
    }

    // GET EVENTS BY CATEGORY
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByCategory(
            Long categoryId
    ) {

        log.debug(
                "Fetching events by category id: {}",
                categoryId
        );

        return eventMapper.toResponseList(
                eventRepository.findByCategoriesId(
                        categoryId
                )
        );
    }

    // GET EVENTS BY VENUE
    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByVenue(
            Long venueId
    ) {

        log.debug(
                "Fetching events by venue id: {}",
                venueId
        );

        return eventMapper.toResponseList(
                eventRepository.findByVenueId(
                        venueId
                )
        );
    }

    // UPDATE EVENT
    @Override
    @Transactional
    public EventResponse updateEvent(
            Long eventId,
            EventUpdateRequest request
    ) {

        Event event =
                findEvent(eventId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Update requested for event id: {} by user id: {}",
                eventId,
                currentUser.getId()
        );

        validateOwnership(
                event,
                currentUser
        );

        Venue venue =
                findVenue(
                        request.getVenue().getVenueName(),
                        request.getVenue().getVenueCity()
                );

        List<Category> categories =
                findCategories(request.getCategories());

        validateEventDates(
                request.getEventStartDateTime(),
                request.getEventEndDateTime()
        );

        validateVenueCapacity(
                request.getEventTotalSeats(),
                venue
        );

        validateStatusTransition(
                event,
                request
        );

        int occupiedSeats =
                event.getEventTotalSeats()
                        - event.getEventAvailableSeats();

        if (request.getEventTotalSeats()
                < occupiedSeats) {

            log.warn(
                    "Event update rejected. Event id: {}, requested seats: {}, occupied seats: {}",
                    eventId,
                    request.getEventTotalSeats(),
                    occupiedSeats
            );

            throw new InvalidOperationException(
                    "Total seats cannot be less than already reserved or sold seats"
            );
        }

        eventMapper.updateEntity(
                request,
                event,
                venue,
                categories
        );

        event.setEventAvailableSeats(
                request.getEventTotalSeats()
                        - occupiedSeats
        );

        Event updatedEvent =
                eventRepository.save(event);

        log.info(
                "Event updated successfully with id: {}",
                updatedEvent.getId()
        );

        return eventMapper.toResponse(updatedEvent);
    }

    // DELETE EVENT
    @Override
    @Transactional
    public void deleteEvent(Long eventId) {

        Event event =
                findEvent(eventId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Delete requested for event id: {} by user id: {}",
                eventId,
                currentUser.getId()
        );

        validateOwnership(
                event,
                currentUser
        );

        eventRepository.delete(event);

        log.info(
                "Event deleted successfully with id: {}",
                eventId
        );
    }

    // --------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------

    private Event findEvent(
            Long eventId
    ) {

        return eventRepository.findById(eventId)
                .orElseThrow(() -> {

                    log.warn(
                            "Event not found with id: {}",
                            eventId
                    );

                    return new ResourceNotFoundException(
                            "Event not found with id: "
                                    + eventId
                    );
                });
    }

    private Venue findVenue(
            String venueName,
            String venueCity
    ) {

        return venueRepository
                .findByVenueNameAndVenueCity(
                        venueName,
                        venueCity
                )
                .orElseThrow(() -> {

                    log.warn(
                            "Venue not found with name '{}' and city '{}'",
                            venueName,
                            venueCity
                    );

                    return new ResourceNotFoundException(
                            "Venue not found: "
                                    + venueName
                                    + " - "
                                    + venueCity
                    );
                });
    }

    private List<Category> findCategories(
            List<CategoryReferenceRequest> categoryRequests
    ) {

        return categoryRequests
                .stream()
                .map(
                        CategoryReferenceRequest::getNameCategory
                )
                .map(name ->
                        categoryRepository
                                .findByNameCategory(name)
                                .orElseThrow(() -> {

                                    log.warn(
                                            "Category not found with name: {}",
                                            name
                                    );

                                    return new ResourceNotFoundException(
                                            "Category not found: "
                                                    + name
                                    );
                                })
                )
                .toList();
    }

    private void validateEventDates(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    ) {

        if (!endDateTime.isAfter(startDateTime)) {

            log.warn(
                    "Invalid event dates. Start: {}, End: {}",
                    startDateTime,
                    endDateTime
            );

            throw new InvalidOperationException(
                    "Event end date must be after start date"
            );
        }
    }

    private void validateVenueCapacity(
            Integer totalSeats,
            Venue venue
    ) {

        if (totalSeats > venue.getVenueCapacity()) {

            log.warn(
                    "Event capacity validation failed. Requested seats: {}, venue capacity: {}, venue id: {}",
                    totalSeats,
                    venue.getVenueCapacity(),
                    venue.getId()
            );

            throw new InvalidOperationException(
                    "Event total seats cannot exceed venue capacity"
            );
        }
    }

    private void validateStatusTransition(
            Event event,
            EventUpdateRequest request
    ) {

        EventStatus currentStatus =
                event.getEventStatus();

        EventStatus newStatus =
                request.getEventStatus();

        log.debug(
                "Validating event status transition for event id: {} from {} to {}",
                event.getId(),
                currentStatus,
                newStatus
        );

        // Nese statusi nuk ndryshon, lejohet
        if (currentStatus == newStatus) {
            return;
        }

        // Event i perfunduar nuk mund te rikthehet
        if (currentStatus == EventStatus.COMPLETED) {

            log.warn(
                    "Invalid status transition for completed event id: {}",
                    event.getId()
            );

            throw new InvalidOperationException(
                    "Completed event status cannot be changed"
            );
        }

        // Event i anuluar nuk mund te riaktivizohet
        if (currentStatus == EventStatus.CANCELLED) {

            log.warn(
                    "Invalid status transition for cancelled event id: {}",
                    event.getId()
            );

            throw new InvalidOperationException(
                    "Cancelled event status cannot be changed"
            );
        }

        // DRAFT -> PUBLISHED ose CANCELLED
        if (currentStatus == EventStatus.DRAFT) {

            if (newStatus != EventStatus.PUBLISHED
                    && newStatus != EventStatus.CANCELLED) {

                log.warn(
                        "Invalid DRAFT status transition for event id: {} to {}",
                        event.getId(),
                        newStatus
                );

                throw new InvalidOperationException(
                        "Draft event can only be published or cancelled"
                );
            }

            return;
        }

        // PUBLISHED -> COMPLETED ose CANCELLED
        if (currentStatus == EventStatus.PUBLISHED) {

            if (newStatus == EventStatus.CANCELLED) {
                return;
            }

            if (newStatus == EventStatus.COMPLETED) {

                if (request.getEventEndDateTime()
                        .isAfter(LocalDateTime.now())) {

                    log.warn(
                            "Event id: {} cannot be completed before end date: {}",
                            event.getId(),
                            request.getEventEndDateTime()
                    );

                    throw new InvalidOperationException(
                            "Event cannot be completed before it has ended"
                    );
                }

                return;
            }

            log.warn(
                    "Invalid PUBLISHED status transition for event id: {} to {}",
                    event.getId(),
                    newStatus
            );

            throw new InvalidOperationException(
                    "Published event can only be completed or cancelled"
            );
        }

        log.warn(
                "Invalid event status transition for event id: {} from {} to {}",
                event.getId(),
                currentStatus,
                newStatus
        );

        throw new InvalidOperationException(
                "Invalid event status transition"
        );
    }

    private void validateOwnership(
            Event event,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        boolean isOwner =
                event.getOrganizer()
                        .getId()
                        .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {

            log.warn(
                    "Unauthorized event modification attempt. Event id: {}, user id: {}",
                    event.getId(),
                    currentUser.getId()
            );

            throw new InvalidOperationException(
                    "You are not allowed to modify this event"
            );
        }

        log.debug(
                "Event ownership validation successful. Event id: {}, user id: {}, admin: {}",
                event.getId(),
                currentUser.getId(),
                isAdmin
        );
    }
}