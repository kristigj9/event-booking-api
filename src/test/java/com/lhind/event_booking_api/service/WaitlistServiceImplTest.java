package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.mapper.WaitlistMapper;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.WaitlistRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitlistServiceImplTest {

    @Mock
    private WaitlistRepository waitlistRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private WaitlistMapper waitlistMapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private WaitlistServiceImpl waitlistService;

    private User user;
    private Event event;
    private Waitlist waitlist;
    private WaitlistResponse response;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.USER)
                .build();

        event = Event.builder()
                .id(10L)
                .eventName("Test Event")
                .eventAvailableSeats(0)
                .eventStatus(EventStatus.PUBLISHED)
                .eventStartDateTime(
                        LocalDateTime.now().plusDays(5)
                )
                .eventEndDateTime(
                        LocalDateTime.now()
                                .plusDays(5)
                                .plusHours(3)
                )
                .build();

        waitlist = Waitlist.builder()
                .id(100L)
                .requestedSeats(2)
                .status(WaitlistStatus.WAITING)
                .user(user)
                .event(event)
                .build();

        response = WaitlistResponse.builder()
                .id(100L)
                .requestedSeats(2)
                .status(WaitlistStatus.WAITING)
                .build();
    }

    @Test
    void joinWaitlist_whenUserAlreadyExists_shouldThrowException() {

        WaitlistRequest request =
                new WaitlistRequest();

        request.setRequestedSeats(2);

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(waitlistRepository
                .existsByUserIdAndEventId(
                        1L,
                        10L
                ))
                .thenReturn(true);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () -> waitlistService.joinWaitlist(
                                10L,
                                request
                        )
                );

        assertEquals(
                "User is already in the waitlist for this event",
                exception.getMessage()
        );

        verify(waitlistRepository, never())
                .save(any(Waitlist.class));
    }

    @Test
    void markAsNotified_shouldCreateNotificationAndChangeStatus() {

        User organizer =
                User.builder()
                        .id(2L)
                        .email("organizer@test.com")
                        .role(Role.ORGANIZER)
                        .build();

        event.setOrganizer(
                organizer
        );

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(organizer);

        when(waitlistRepository.findById(100L))
                .thenReturn(
                        Optional.of(waitlist)
                );

        when(waitlistRepository.save(waitlist))
                .thenReturn(waitlist);

        WaitlistResponse notifiedResponse =
                WaitlistResponse.builder()
                        .id(100L)
                        .requestedSeats(2)
                        .status(WaitlistStatus.NOTIFIED)
                        .build();

        when(waitlistMapper.toResponse(waitlist))
                .thenReturn(notifiedResponse);

        WaitlistResponse result =
                waitlistService.markAsNotified(
                        100L
                );

        assertEquals(
                WaitlistStatus.NOTIFIED,
                waitlist.getStatus()
        );

        assertEquals(
                WaitlistStatus.NOTIFIED,
                result.getStatus()
        );

        verify(waitlistRepository)
                .save(waitlist);

        verify(notificationService)
                .createNotification(
                        eq(user),
                        eq(event),
                        isNull(),
                        eq(NotificationType.WAITLIST_AVAILABLE),
                        anyString()
                );
    }
}