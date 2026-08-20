package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private BookingRepository bookingRepository;


    @Test
    void findByUserIdOrderByCreatedAtDesc_shouldReturnNotificationsNewestFirst() {

        User user = createAndSaveUser(
                "notification1@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Notification Venue 1"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Notification Event 1"
        );

        Notification oldNotification =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.WAITLIST_AVAILABLE
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Old notification")
                        .createdAt(
                                LocalDateTime.now()
                                        .minusMinutes(10)
                        )
                        .build();

        Notification newNotification =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.WAITLIST_AVAILABLE
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("New notification")
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        notificationRepository.save(oldNotification);
        notificationRepository.save(newNotification);

        List<Notification> result =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                user.getId()
                        );

        assertEquals(2, result.size());

        assertEquals(
                "New notification",
                result.get(0).getMessage()
        );

        assertEquals(
                "Old notification",
                result.get(1).getMessage()
        );
    }


    @Test
    void findByUserIdAndNotificationStatusOrderByCreatedAtDesc_shouldReturnUnreadNotifications() {

        User user = createAndSaveUser(
                "notification2@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Notification Venue 2"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Notification Event 2"
        );

        Notification unread =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.BOOKING_CONFIRMED
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Unread notification")
                        .build();

        Notification read =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.BOOKING_CANCELLED
                        )
                        .notificationStatus(
                                NotificationStatus.READ
                        )
                        .message("Read notification")
                        .readAt(LocalDateTime.now())
                        .build();

        notificationRepository.save(unread);
        notificationRepository.save(read);

        List<Notification> result =
                notificationRepository
                        .findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
                                user.getId(),
                                NotificationStatus.UNREAD
                        );

        assertEquals(1, result.size());

        assertEquals(
                NotificationStatus.UNREAD,
                result.get(0).getNotificationStatus()
        );

        assertEquals(
                "Unread notification",
                result.get(0).getMessage()
        );
    }


    @Test
    void countByUserIdAndNotificationStatus_shouldReturnUnreadCount() {

        User user = createAndSaveUser(
                "notification3@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Notification Venue 3"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Notification Event 3"
        );

        Notification firstUnread =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.PAYMENT_COMPLETED
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("First unread")
                        .build();

        Notification secondUnread =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.PAYMENT_REFUNDED
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Second unread")
                        .build();

        Notification read =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.BOOKING_COMPLETED
                        )
                        .notificationStatus(
                                NotificationStatus.READ
                        )
                        .message("Read notification")
                        .readAt(LocalDateTime.now())
                        .build();

        notificationRepository.save(firstUnread);
        notificationRepository.save(secondUnread);
        notificationRepository.save(read);

        long result =
                notificationRepository
                        .countByUserIdAndNotificationStatus(
                                user.getId(),
                                NotificationStatus.UNREAD
                        );

        assertEquals(2L, result);
    }


    @Test
    void findByEventIdOrderByCreatedAtDesc_shouldReturnEventNotifications() {

        User user = createAndSaveUser(
                "notification4@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Notification Venue 4"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Notification Event 4"
        );

        Notification notification =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .notificationType(
                                NotificationType.WAITLIST_AVAILABLE
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Event notification")
                        .build();

        notificationRepository.save(notification);

        List<Notification> result =
                notificationRepository
                        .findByEventIdOrderByCreatedAtDesc(
                                event.getId()
                        );

        assertEquals(1, result.size());

        assertEquals(
                event.getId(),
                result.get(0)
                        .getEvent()
                        .getId()
        );
    }


    @Test
    void findByBookingIdOrderByCreatedAtDesc_shouldReturnBookingNotifications() {

        User user = createAndSaveUser(
                "notification5@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Notification Venue 5"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Notification Event 5"
        );

        Booking booking =
                createAndSaveBooking(
                        user,
                        event
                );

        Notification notification =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .booking(booking)
                        .notificationType(
                                NotificationType.BOOKING_CONFIRMED
                        )
                        .notificationStatus(
                                NotificationStatus.UNREAD
                        )
                        .message("Booking notification")
                        .build();

        notificationRepository.save(notification);

        List<Notification> result =
                notificationRepository
                        .findByBookingIdOrderByCreatedAtDesc(
                                booking.getId()
                        );

        assertEquals(1, result.size());

        assertEquals(
                booking.getId(),
                result.get(0)
                        .getBooking()
                        .getId()
        );
    }


    // --------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------

    private User createAndSaveUser(
            String email
    ) {

        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }


    private Venue createAndSaveVenue(
            String venueName
    ) {

        Venue venue = Venue.builder()
                .venueName(venueName)
                .venueAddress("Test Address")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        return venueRepository.save(venue);
    }


    private Event createAndSaveEvent(
            User organizer,
            Venue venue,
            String eventName
    ) {

        Event event = Event.builder()
                .eventName(eventName)
                .eventDescription("Test Description")
                .eventStartDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                )
                .eventEndDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                                .plusHours(2)
                )
                .eventTotalSeats(100)
                .eventAvailableSeats(100)
                .eventStatus(
                        EventStatus.PUBLISHED
                )
                .venue(venue)
                .organizer(organizer)
                .build();

        return eventRepository.save(event);
    }


    private Booking createAndSaveBooking(
            User user,
            Event event
    ) {

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .bookingStatus(
                        BookingStatus.PENDING
                )
                .seatsBooked(1)
                .build();

        return bookingRepository.save(booking);
    }
}