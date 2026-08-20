package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.NotificationMapper;
import com.lhind.event_booking_api.repository.NotificationRepository;
import com.lhind.event_booking_api.repository.UserRepository;
import com.lhind.event_booking_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;


    @Override
    public NotificationResponse createNotification(
            User user,
            Event event,
            Booking booking,
            NotificationType notificationType,
            String message
    ) {

        Notification notification = Notification.builder()
                .user(user)
                .event(event)
                .booking(booking)
                .notificationType(notificationType)
                .notificationStatus(NotificationStatus.UNREAD)
                .message(message)
                .build();

        Notification savedNotification =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(
                savedNotification
        );
    }


    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(
            Long notificationId
    ) {

        Notification notification =
                findNotificationById(notificationId);

        User currentUser = getCurrentUser();

        validateOwnerOrAdmin(
                notification,
                currentUser
        );

        return notificationMapper.toResponse(
                notification
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {

        User currentUser = getCurrentUser();

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                currentUser.getId()
                        );

        return notificationMapper.toResponseList(
                notifications
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyUnreadNotifications() {

        User currentUser = getCurrentUser();

        List<Notification> notifications =
                notificationRepository
                        .findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
                                currentUser.getId(),
                                NotificationStatus.UNREAD
                        );

        return notificationMapper.toResponseList(
                notifications
        );
    }


    @Override
    @Transactional(readOnly = true)
    public long countMyUnreadNotifications() {

        User currentUser = getCurrentUser();

        return notificationRepository
                .countByUserIdAndNotificationStatus(
                        currentUser.getId(),
                        NotificationStatus.UNREAD
                );
    }


    @Override
    public NotificationResponse markAsRead(
            Long notificationId
    ) {

        Notification notification =
                findNotificationById(notificationId);

        User currentUser = getCurrentUser();

        validateOwnerOrAdmin(
                notification,
                currentUser
        );

        if (notification.getNotificationStatus()
                == NotificationStatus.READ) {

            return notificationMapper.toResponse(
                    notification
            );
        }

        notification.setNotificationStatus(
                NotificationStatus.READ
        );

        notification.setReadAt(
                LocalDateTime.now()
        );

        Notification updatedNotification =
                notificationRepository.save(notification);

        return notificationMapper.toResponse(
                updatedNotification
        );
    }


    @Override
    public void markAllAsRead() {

        User currentUser = getCurrentUser();

        List<Notification> unreadNotifications =
                notificationRepository
                        .findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
                                currentUser.getId(),
                                NotificationStatus.UNREAD
                        );

        LocalDateTime now =
                LocalDateTime.now();

        unreadNotifications.forEach(notification -> {

            notification.setNotificationStatus(
                    NotificationStatus.READ
            );

            notification.setReadAt(now);
        });

        notificationRepository.saveAll(
                unreadNotifications
        );
    }


    // PRIVATE METHODS

    private Notification findNotificationById(
            Long notificationId
    ) {

        return notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with id: "
                                        + notificationId
                        )
                );
    }


    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: "
                                        + email
                        )
                );
    }


    private void validateOwnerOrAdmin(
            Notification notification,
            User currentUser
    ) {

        boolean isOwner =
                notification.getUser()
                        .getId()
                        .equals(currentUser.getId());

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        if (!isOwner && !isAdmin) {

            throw new IllegalStateException(
                    "You are not authorized to access this notification"
            );
        }
    }
}