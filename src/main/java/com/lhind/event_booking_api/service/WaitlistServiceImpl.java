package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.WaitlistMapper;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.UserRepository;
import com.lhind.event_booking_api.repository.WaitlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WaitlistServiceImpl implements WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final WaitlistMapper waitlistMapper;
    private final NotificationService notificationService;


    @Override
    public WaitlistResponse joinWaitlist(
            Long eventId,
            WaitlistRequest request
    ) {

        User currentUser = getCurrentUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + eventId
                        )
                );

        if (waitlistRepository.existsByUserIdAndEventId(
                currentUser.getId(),
                eventId
        )) {
            throw new IllegalStateException(
                    "User is already in the waitlist for this event"
            );
        }

        // Waitlist perdoret vetem kur nuk ka vende te mjaftueshme
        if (event.getEventAvailableSeats()
                >= request.getRequestedSeats()) {

            throw new IllegalStateException(
                    "Enough seats are available. Booking can be created directly"
            );
        }

        Waitlist waitlist = waitlistMapper.toEntity(
                request,
                currentUser,
                event
        );

        Waitlist savedWaitlist =
                waitlistRepository.save(waitlist);

        return waitlistMapper.toResponse(savedWaitlist);
    }


    @Override
    @Transactional(readOnly = true)
    public WaitlistResponse getWaitlistById(
            Long waitlistId
    ) {

        Waitlist waitlist = findWaitlistById(waitlistId);

        User currentUser = getCurrentUser();

        boolean isOwner =
                waitlist.getUser()
                        .getId()
                        .equals(currentUser.getId());

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        boolean isEventOrganizer =
                waitlist.getEvent().getOrganizer() != null
                        && waitlist.getEvent()
                        .getOrganizer()
                        .getId()
                        .equals(currentUser.getId());

        if (!isOwner && !isAdmin && !isEventOrganizer) {
            throw new IllegalStateException(
                    "You are not authorized to view this waitlist"
            );
        }

        return waitlistMapper.toResponse(waitlist);
    }
    @Override
    @Transactional(readOnly = true)
    public List<WaitlistResponse> getMyWaitlists() {

        User currentUser = getCurrentUser();

        List<Waitlist> waitlists =
                waitlistRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                currentUser.getId()
                        );

        return waitlistMapper.toResponseList(waitlists);
    }


    @Override
    @Transactional(readOnly = true)
    public List<WaitlistResponse> getWaitlistByEvent(
            Long eventId
    ) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + eventId
                        )
                );

        validateOrganizerOrAdmin(event);

        List<Waitlist> waitlists =
                waitlistRepository
                        .findByEventIdOrderByCreatedAtAsc(
                                eventId
                        );

        return waitlistMapper.toResponseList(waitlists);
    }


    @Override
    @Transactional(readOnly = true)
    public List<WaitlistResponse> getWaitlistByEventAndStatus(
            Long eventId,
            WaitlistStatus status
    ) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Event not found with id: " + eventId
                        )
                );

        validateOrganizerOrAdmin(event);

        List<Waitlist> waitlists =
                waitlistRepository
                        .findByEventIdAndStatusOrderByCreatedAtAsc(
                                eventId,
                                status
                        );

        return waitlistMapper.toResponseList(waitlists);
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

        if (waitlist.getStatus() != WaitlistStatus.WAITING) {
            throw new IllegalStateException(
                    "Only WAITING waitlist can be marked as NOTIFIED"
            );
        }

        waitlist.setStatus(
                WaitlistStatus.NOTIFIED
        );

        Waitlist updatedWaitlist =
                waitlistRepository.save(waitlist);

        notificationService.createNotification(
                waitlist.getUser(),
                waitlist.getEvent(),
                null,
                NotificationType.WAITLIST_AVAILABLE,
                "Seats are now available for event: "
                        + waitlist.getEvent().getEventName()
        );

        return waitlistMapper.toResponse(
                updatedWaitlist
        );
    }

    @Override
    public WaitlistResponse markAsConverted(Long waitlistId) {

        Waitlist waitlist =
                findWaitlistById(waitlistId);

        validateOrganizerOrAdmin(
                waitlist.getEvent()
        );

        if (waitlist.getStatus() != WaitlistStatus.NOTIFIED) {
            throw new IllegalStateException(
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

        Waitlist waitlist = findWaitlistById(waitlistId);

        User currentUser = getCurrentUser();

        if (!waitlist.getUser().getId()
                .equals(currentUser.getId())) {

            throw new IllegalStateException(
                    "You cannot cancel another user's waitlist"
            );
        }

        if (waitlist.getStatus() == WaitlistStatus.CONVERTED) {
            throw new IllegalStateException(
                    "Converted waitlist cannot be cancelled"
            );
        }

        if (waitlist.getStatus() == WaitlistStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Waitlist is already cancelled"
            );
        }

        waitlist.setStatus(WaitlistStatus.CANCELLED);

        Waitlist updatedWaitlist =
                waitlistRepository.save(waitlist);

        return waitlistMapper.toResponse(updatedWaitlist);
    }


    private void validateOrganizerOrAdmin(Event event) {

        User currentUser = getCurrentUser();

        boolean isAdmin =
                currentUser.getRole() == Role.ADMIN;

        boolean isOrganizer =
                event.getOrganizer() != null
                        && event.getOrganizer()
                        .getId()
                        .equals(currentUser.getId());

        if (!isAdmin && !isOrganizer) {
            throw new IllegalStateException(
                    "Only the event organizer or admin can perform this action"
            );
        }
    }

    // PRIVATE METHODS

    private Waitlist findWaitlistById(
            Long waitlistId
    ) {

        return waitlistRepository.findById(waitlistId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Waitlist not found with id: "
                                        + waitlistId
                        )
                );
    }


    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );
    }
}