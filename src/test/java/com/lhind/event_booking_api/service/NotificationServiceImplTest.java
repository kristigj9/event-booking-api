package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.mapper.NotificationMapper;
import com.lhind.event_booking_api.repository.NotificationRepository;
import com.lhind.event_booking_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Event event;
    private Booking booking;
    private Notification notification;
    private NotificationResponse response;

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
                .build();

        booking = Booking.builder()
                .id(20L)
                .user(user)
                .event(event)
                .bookingStatus(BookingStatus.CONFIRMED)
                .build();

        notification = Notification.builder()
                .id(100L)
                .user(user)
                .event(event)
                .booking(booking)
                .notificationType(
                        NotificationType.BOOKING_CONFIRMED
                )
                .notificationStatus(
                        NotificationStatus.UNREAD
                )
                .message("Booking confirmed")
                .build();

        response = NotificationResponse.builder()
                .id(100L)
                .notificationType(
                        NotificationType.BOOKING_CONFIRMED
                )
                .notificationStatus(
                        NotificationStatus.UNREAD
                )
                .message("Booking confirmed")
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                "user@test.com",
                                null
                        )
                );
    }


    // CREATE

    @Test
    void createNotification_shouldCreateUnreadNotification() {

        when(notificationRepository.save(any(Notification.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        when(notificationMapper.toResponse(any(Notification.class)))
                .thenReturn(response);

        NotificationResponse result =
                notificationService.createNotification(
                        user,
                        event,
                        booking,
                        NotificationType.BOOKING_CONFIRMED,
                        "Booking confirmed"
                );

        assertNotNull(result);

        verify(notificationRepository).save(
                argThat(saved ->
                        saved.getUser() == user
                                && saved.getEvent() == event
                                && saved.getBooking() == booking
                                && saved.getNotificationType()
                                == NotificationType.BOOKING_CONFIRMED
                                && saved.getNotificationStatus()
                                == NotificationStatus.UNREAD
                                && saved.getMessage()
                                .equals("Booking confirmed")
                )
        );
    }


    // GET BY ID

    @Test
    void getNotificationById_shouldReturnNotificationForOwner() {

        when(notificationRepository.findById(100L))
                .thenReturn(Optional.of(notification));

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        NotificationResponse result =
                notificationService.getNotificationById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());

        verify(notificationRepository)
                .findById(100L);

        verify(notificationMapper)
                .toResponse(notification);
    }


    // GET MY NOTIFICATIONS

    @Test
    void getMyNotifications_shouldReturnCurrentUserNotifications() {

        List<Notification> notifications =
                List.of(notification);

        List<NotificationResponse> responses =
                List.of(response);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository
                .findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(notifications);

        when(notificationMapper.toResponseList(notifications))
                .thenReturn(responses);

        List<NotificationResponse> result =
                notificationService.getMyNotifications();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(notificationRepository)
                .findByUserIdOrderByCreatedAtDesc(1L);
    }


    // GET UNREAD

    @Test
    void getMyUnreadNotifications_shouldReturnOnlyUnreadNotifications() {

        List<Notification> notifications =
                List.of(notification);

        List<NotificationResponse> responses =
                List.of(response);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository
                .findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
                        1L,
                        NotificationStatus.UNREAD
                ))
                .thenReturn(notifications);

        when(notificationMapper.toResponseList(notifications))
                .thenReturn(responses);

        List<NotificationResponse> result =
                notificationService
                        .getMyUnreadNotifications();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(notificationRepository)
                .findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
                        1L,
                        NotificationStatus.UNREAD
                );
    }


    // COUNT UNREAD

    @Test
    void countMyUnreadNotifications_shouldReturnUnreadCount() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository
                .countByUserIdAndNotificationStatus(
                        1L,
                        NotificationStatus.UNREAD
                ))
                .thenReturn(3L);

        long result =
                notificationService
                        .countMyUnreadNotifications();

        assertEquals(3L, result);

        verify(notificationRepository)
                .countByUserIdAndNotificationStatus(
                        1L,
                        NotificationStatus.UNREAD
                );
    }


    // MARK AS READ

    @Test
    void markAsRead_shouldChangeUnreadNotificationToRead() {

        when(notificationRepository.findById(100L))
                .thenReturn(Optional.of(notification));

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository.save(notification))
                .thenReturn(notification);

        when(notificationMapper.toResponse(notification))
                .thenAnswer(invocation -> {

                    Notification n =
                            invocation.getArgument(0);

                    return NotificationResponse.builder()
                            .id(n.getId())
                            .notificationStatus(
                                    n.getNotificationStatus()
                            )
                            .readAt(n.getReadAt())
                            .build();
                });

        NotificationResponse result =
                notificationService.markAsRead(100L);

        assertEquals(
                NotificationStatus.READ,
                result.getNotificationStatus()
        );

        assertEquals(
                NotificationStatus.READ,
                notification.getNotificationStatus()
        );

        assertNotNull(
                notification.getReadAt()
        );

        verify(notificationRepository)
                .save(notification);
    }


    @Test
    void markAsRead_shouldNotSaveWhenAlreadyRead() {

        notification.setNotificationStatus(
                NotificationStatus.READ
        );

        when(notificationRepository.findById(100L))
                .thenReturn(Optional.of(notification));

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(notificationMapper.toResponse(notification))
                .thenReturn(response);

        notificationService.markAsRead(100L);

        verify(notificationRepository, never())
                .save(any(Notification.class));
    }


    // MARK ALL AS READ

    @Test
    void markAllAsRead_shouldMarkAllUnreadNotificationsAsRead() {

        Notification secondNotification =
                Notification.builder()
                        .id(101L)
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.PAYMENT_COMPLETED
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Payment completed")
                        .build();

        List<Notification> notifications =
                List.of(
                        notification,
                        secondNotification
                );

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(notificationRepository
                .findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
                        1L,
                        NotificationStatus.UNREAD
                ))
                .thenReturn(notifications);

        notificationService.markAllAsRead();

        assertEquals(
                NotificationStatus.READ,
                notification.getNotificationStatus()
        );

        assertEquals(
                NotificationStatus.READ,
                secondNotification.getNotificationStatus()
        );

        assertNotNull(
                notification.getReadAt()
        );

        assertNotNull(
                secondNotification.getReadAt()
        );

        verify(notificationRepository)
                .saveAll(notifications);
    }
}