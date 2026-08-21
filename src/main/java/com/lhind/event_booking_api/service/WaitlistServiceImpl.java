package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.WaitlistMapper;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.WaitlistRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WaitlistServiceImpl implements WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final EventRepository eventRepository;
    private final WaitlistMapper waitlistMapper;
    private final NotificationService notificationService;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    public WaitlistResponse joinWaitlist(
            Long eventId,
            WaitlistRequest request
    ) {

        User currentUser =
                authenticatedUserService.getCurrentUser();

        Event event =
                findEvent(eventId);

        if (event.getEventStatus()
                != EventStatus.PUBLISHED) {

            throw new InvalidOperationException(
                    "Waitlist is available only for published events"
            );
        }

        if (!event.getEventStartDateTime()
                .isAfter(LocalDateTime.now())) {

            throw new InvalidOperationException(
                    "Waitlist cannot be joined after the event has started"
            );
        }

        if (waitlistRepository
                .existsByUserIdAndEventId(
                        currentUser.getId(),
                        eventId
                )) {

            throw new InvalidOperationException(
                    "User is already in the waitlist for this event"
            );
        }

        // Waitlist perdoret vetem kur nuk ka vende te mjaftueshme
        if (event.getEventAvailableSeats()
                >= request.getRequestedSeats()) {

            throw new InvalidOperationException(
                    "Enough seats are available. Booking can be created directly"
            );
        }

        Waitlist waitlist =
                waitlistMapper.toEntity(
                        request,
                        currentUser,
                        event
                );

        Waitlist savedWaitlist =
                waitlistRepository.save(waitlist);

        return waitlistMapper.toResponse(
                savedWaitlist
        );
    }

    @Override
    @Transactional(readOnly = true)
    public WaitlistResponse getWaitlistById(
            Long waitlistId
    ) {

        Waitlist waitlist =
                findWaitlistById(waitlistId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        boolean isOwner =
                waitlist.getUser()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        boolean isEventOrganizer =
                waitlist.getEvent()
                        .getOrganizer() != null
                        && waitlist.getEvent()
                        .getOrganizer()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        if (!isOwner
                && !isAdmin
                && !isEventOrganizer) {

            throw new InvalidOperationException(
                    "You are not authorized to view this waitlist"
            );
        }

        return waitlistMapper.toResponse(
                waitlist
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<WaitlistResponse> getMyWaitlists() {

        User currentUser =
                authenticatedUserService.getCurrentUser();

        return waitlistMapper.toResponseList(
                waitlistRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                currentUser.getId()
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<WaitlistResponse> getWaitlistByEvent(
            Long eventId
    ) {

        Event event =
                findEvent(eventId);

        validateOrganizerOrAdmin(
                event
        );

        return waitlistMapper.toResponseList(
                waitlistRepository
                        .findByEventIdOrderByCreatedAtAsc(
                                eventId
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<WaitlistResponse> getWaitlistByEventAndStatus(
            Long eventId,
            WaitlistStatus status
    ) {

        Event event =
                findEvent(eventId);

        validateOrganizerOrAdmin(
                event
        );

        return waitlistMapper.toResponseList(
                waitlistRepository
                        .findByEventIdAndStatusOrderByCreatedAtAsc(
                                eventId,
                                status
                        )
        );
    }

    @Override
    public WaitlistResponse markAsNotified(
            Long waitlistId
    ) {

        Waitlist waitlist =
                findWaitlistById(waitlistId);

        validateOrganizerOrAdmin(
                waitlist.getEvent()
        );

        if (waitlist.getStatus()
                != WaitlistStatus.WAITING) {

            throw new InvalidOperationException(
                    "Only WAITING waitlist can be marked as NOTIFIED"
            );
        }

        waitlist.setStatus(
                WaitlistStatus.NOTIFIED
        );

        Waitlist updatedWaitlist =
                waitlistRepository.save(waitlist);

        notificationService.createNotification(
                updatedWaitlist.getUser(),
                updatedWaitlist.getEvent(),
                null,
                NotificationType.WAITLIST_AVAILABLE,
                "Seats are now available for event: "
                        + updatedWaitlist.getEvent()
                        .getEventName()
        );

        return waitlistMapper.toResponse(
                updatedWaitlist
        );
    }

    @Override
    public WaitlistResponse markAsConverted(
            Long waitlistId
    ) {

        Waitlist waitlist =
                findWaitlistById(waitlistId);

        validateOrganizerOrAdmin(
                waitlist.getEvent()
        );

        if (waitlist.getStatus()
                != WaitlistStatus.NOTIFIED) {

            throw new InvalidOperationException(
                    "Only NOTIFIED waitlist can be converted"
            );
        }

        waitlist.setStatus(
                WaitlistStatus.CONVERTED
        );

        Waitlist updatedWaitlist =
                waitlistRepository.save(waitlist);

        return waitlistMapper.toResponse(
                updatedWaitlist
        );
    }

    @Override
    public WaitlistResponse cancelWaitlist(
            Long waitlistId
    ) {

        Waitlist waitlist =
                findWaitlistById(waitlistId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        if (!waitlist.getUser()
                .getId()
                .equals(
                        currentUser.getId()
                )) {

            throw new InvalidOperationException(
                    "You cannot cancel another user's waitlist"
            );
        }

        if (waitlist.getStatus()
                == WaitlistStatus.CONVERTED) {

            throw new InvalidOperationException(
                    "Converted waitlist cannot be cancelled"
            );
        }

        if (waitlist.getStatus()
                == WaitlistStatus.CANCELLED) {

            throw new InvalidOperationException(
                    "Waitlist is already cancelled"
            );
        }

        waitlist.setStatus(
                WaitlistStatus.CANCELLED
        );

        Waitlist updatedWaitlist =
                waitlistRepository.save(waitlist);

        return waitlistMapper.toResponse(
                updatedWaitlist
        );
    }

    // PRIVATE HELPER METHODS

    private Event findEvent(
            Long eventId
    ) {

        return eventRepository
                .findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: "
                                        + eventId
                        )
                );
    }

    private Waitlist findWaitlistById(
            Long waitlistId
    ) {

        return waitlistRepository
                .findById(waitlistId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Waitlist not found with id: "
                                        + waitlistId
                        )
                );
    }

    private void validateOrganizerOrAdmin(
            Event event
    ) {

        User currentUser =
                authenticatedUserService.getCurrentUser();

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        boolean isOrganizer =
                event.getOrganizer() != null
                        && event.getOrganizer()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        if (!isAdmin && !isOrganizer) {

            throw new InvalidOperationException(
                    "Only the event organizer or admin can perform this action"
            );
        }
    }
}