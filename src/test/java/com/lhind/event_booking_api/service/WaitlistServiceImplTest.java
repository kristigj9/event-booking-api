package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.mapper.WaitlistMapper;
import com.lhind.event_booking_api.repository.EventRepository;
import com.lhind.event_booking_api.repository.UserRepository;
import com.lhind.event_booking_api.repository.WaitlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitlistServiceImplTest {

    @Mock
    private WaitlistRepository waitlistRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WaitlistMapper waitlistMapper;

    @InjectMocks
    private WaitlistServiceImpl waitlistService;
    @Mock
    private NotificationService notificationService;

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

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "user@test.com",
                                null
                        )
                );


    }
    @Test
    void joinWaitlist_whenUserAlreadyExists_shouldThrowException() {

        WaitlistRequest request = new WaitlistRequest();
        request.setRequestedSeats(2);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(waitlistRepository.existsByUserIdAndEventId(
                1L,
                10L
        )).thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> waitlistService.joinWaitlist(
                        10L,
                        request
                )
        );

        verify(waitlistRepository, never())
                .save(any());
    }

    @Test
    void markAsNotified_shouldCreateNotificationAndChangeStatus() {

        User organizer = User.builder()
                .id(2L)
                .email("organizer@test.com")
                .role(Role.ORGANIZER)
                .build();

        event.setOrganizer(organizer);

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "organizer@test.com",
                                null
                        )
                );

        when(userRepository.findByEmail("organizer@test.com"))
                .thenReturn(Optional.of(organizer));

        when(waitlistRepository.findById(100L))
                .thenReturn(Optional.of(waitlist));

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
                waitlistService.markAsNotified(100L);

        assertEquals(
                WaitlistStatus.NOTIFIED,
                waitlist.getStatus()
        );

        assertEquals(
                WaitlistStatus.NOTIFIED,
                result.getStatus()
        );

        verify(waitlistRepository).save(waitlist);

        verify(notificationService).createNotification(
                eq(user),
                eq(event),
                isNull(),
                eq(NotificationType.WAITLIST_AVAILABLE),
                anyString()
        );
    }
}