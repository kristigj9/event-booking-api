package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.notification.NotificationResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.NotificationMapper;
import com.lhind.event_booking_api.repository.NotificationRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private static final Logger log =
            LogManager.getLogger(
                    NotificationServiceImpl.class
            );

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AuthenticatedUserService authenticatedUserService;

    // CREATE NOTIFICATION
    @Override
    public NotificationResponse createNotification(
            User user,
            Event event,
            Booking booking,
            NotificationType notificationType,
            String message
    ) {

        if (user == null) {

            throw new InvalidOperationException(
                    "Notification user is required"
            );
        }

        if (notificationType == null) {

            throw new InvalidOperationException(
                    "Notification type is required"
            );
        }

        if (message == null
                || message.isBlank()) {

            throw new InvalidOperationException(
                    "Notification message is required"
            );
        }

        log.info(
                "Creating notification of type: {} for user id: {}",
                notificationType,
                user.getId()
        );

        Notification notification =
                Notification.builder()
                        .user(user)
                        .event(event)
                        .booking(booking)
                        .notificationType(
                                notificationType
                        )
                        .message(message)
                        .build();

        Notification savedNotification =
                notificationRepository.save(
                        notification
                );

        log.info(
                "Notification created successfully with id: {} for user id: {}",
                savedNotification.getId(),
                user.getId()
        );

        return notificationMapper.toResponse(
                savedNotification
        );
    }

    // GET NOTIFICATION BY ID
    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(
            Long notificationId
    ) {

        Notification notification =
                findNotificationById(
                        notificationId
                );

        User currentUser =
                authenticatedUserService
                        .getCurrentUser();

        validateOwnerOrAdmin(
                notification,
                currentUser
        );

        return notificationMapper.toResponse(
                notification
        );
    }

    // GET CURRENT USER NOTIFICATIONS
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {

        User currentUser =
                authenticatedUserService
                        .getCurrentUser();

        log.debug(
                "Fetching notifications for user id: {}",
                currentUser.getId()
        );

        return notificationMapper.toResponseList(
                notificationRepository
                        .findByUserIdOrderByCreatedAtDesc(
                                currentUser.getId()
                        )
        );
    }

    // GET CURRENT USER UNREAD NOTIFICATIONS
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyUnreadNotifications() {

        User currentUser =
                authenticatedUserService
                        .getCurrentUser();

        log.debug(
                "Fetching unread notifications for user id: {}",
                currentUser.getId()
        );

        return notificationMapper.toResponseList(
                notificationRepository
                        .findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
                                currentUser.getId(),
                                NotificationStatus.UNREAD
                        )
        );
    }

    // COUNT CURRENT USER UNREAD NOTIFICATIONS
    @Override
    @Transactional(readOnly = true)
    public long countMyUnreadNotifications() {

        User currentUser =
                authenticatedUserService
                        .getCurrentUser();

        return notificationRepository
                .countByUserIdAndNotificationStatus(
                        currentUser.getId(),
                        NotificationStatus.UNREAD
                );
    }

    // MARK ONE NOTIFICATION AS READ
    @Override
    public NotificationResponse markAsRead(
            Long notificationId
    ) {

        Notification notification =
                findNotificationById(
                        notificationId
                );

        User currentUser =
                authenticatedUserService
                        .getCurrentUser();

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
                notificationRepository.save(
                        notification
                );

        log.info(
                "Notification id: {} marked as READ by user id: {}",
                notificationId,
                currentUser.getId()
        );

        return notificationMapper.toResponse(
                updatedNotification
        );
    }

    // MARK ALL CURRENT USER NOTIFICATIONS AS READ
    @Override
    public void markAllAsRead() {

        User currentUser =
                authenticatedUserService
                        .getCurrentUser();

        List<Notification> unreadNotifications =
                notificationRepository
                        .findByUserIdAndNotificationStatusOrderByCreatedAtDesc(
                                currentUser.getId(),
                                NotificationStatus.UNREAD
                        );

        if (unreadNotifications.isEmpty()) {

            log.debug(
                    "No unread notifications found for user id: {}",
                    currentUser.getId()
            );

            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        unreadNotifications.forEach(
                notification -> {

                    notification.setNotificationStatus(
                            NotificationStatus.READ
                    );

                    notification.setReadAt(now);
                }
        );

        notificationRepository.saveAll(
                unreadNotifications
        );

        log.info(
                "{} notifications marked as READ for user id: {}",
                unreadNotifications.size(),
                currentUser.getId()
        );
    }

    // --------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------

    private Notification findNotificationById(
            Long notificationId
    ) {

        return notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> {

                    log.warn(
                            "Notification not found with id: {}",
                            notificationId
                    );

                    return new ResourceNotFoundException(
                            "Notification not found with id: "
                                    + notificationId
                    );
                });
    }

    private void validateOwnerOrAdmin(
            Notification notification,
            User currentUser
    ) {

        boolean isOwner =
                notification.getUser()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        if (!isOwner && !isAdmin) {

            log.warn(
                    "Unauthorized notification access attempt. Notification id: {}, user id: {}",
                    notification.getId(),
                    currentUser.getId()
            );

            throw new InvalidOperationException(
                    "You are not authorized to access this notification"
            );
        }
    }
}