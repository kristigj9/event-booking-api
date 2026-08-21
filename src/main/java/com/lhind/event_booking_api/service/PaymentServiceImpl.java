package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.PaymentMapper;
import com.lhind.event_booking_api.repository.BookingRepository;
import com.lhind.event_booking_api.repository.PaymentRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log =
            LogManager.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final AuthenticatedUserService authenticatedUserService;
    private final NotificationService notificationService;

    // CREATE PAYMENT
    @Override
    @Transactional
    public PaymentResponse createPayment(
            Long bookingId,
            PaymentRequest request
    ) {

        log.info(
                "Payment creation requested for booking id: {}",
                bookingId
        );

        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Booking not found with id: {}",
                                    bookingId
                            );

                            return new ResourceNotFoundException(
                                    "Booking not found with id: "
                                            + bookingId
                            );
                        });

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validateBookingOwnership(
                booking,
                currentUser
        );

        if (paymentRepository
                .existsByBookingId(bookingId)) {

            log.warn(
                    "Payment creation rejected because payment already exists for booking id: {}",
                    bookingId
            );

            throw new InvalidOperationException(
                    "Payment already exists for booking: "
                            + bookingId
            );
        }

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

            log.warn(
                    "Payment creation rejected because booking id: {} is cancelled",
                    bookingId
            );

            throw new InvalidOperationException(
                    "Payment cannot be created for a cancelled booking"
            );
        }

        if (booking.getBookingStatus()
                == BookingStatus.COMPLETED) {

            log.warn(
                    "Payment creation rejected because booking id: {} is completed",
                    bookingId
            );

            throw new InvalidOperationException(
                    "Payment cannot be created for a completed booking"
            );
        }

        BigDecimal amount =
                calculateAmount(booking);

        Payment payment =
                paymentMapper.toEntity(request);

        payment.setBooking(
                booking
        );

        payment.setAmount(
                amount
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        log.info(
                "Payment created successfully with id: {} for booking id: {}",
                savedPayment.getId(),
                bookingId
        );

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    // GET PAYMENT BY ID
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            Long paymentId
    ) {

        log.debug(
                "Fetching payment by id: {}",
                paymentId
        );

        Payment payment =
                findPayment(paymentId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validatePaymentAccess(
                payment,
                currentUser
        );

        return paymentMapper.toResponse(
                payment
        );
    }

    // GET PAYMENT BY BOOKING
    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(
            Long bookingId
    ) {

        log.debug(
                "Fetching payment for booking id: {}",
                bookingId
        );

        Payment payment =
                paymentRepository
                        .findByBookingId(bookingId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Payment not found for booking id: {}",
                                    bookingId
                            );

                            return new ResourceNotFoundException(
                                    "Payment not found for booking: "
                                            + bookingId
                            );
                        });

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validatePaymentAccess(
                payment,
                currentUser
        );

        return paymentMapper.toResponse(
                payment
        );
    }

    // COMPLETE PAYMENT
    @Override
    @Transactional
    public PaymentResponse completePayment(
            Long paymentId
    ) {

        Payment payment =
                findPayment(paymentId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Payment completion requested for payment id: {} by user id: {}",
                paymentId,
                currentUser.getId()
        );

        validatePaymentAccess(
                payment,
                currentUser
        );

        if (payment.getPaymentStatus()
                == PaymentStatus.COMPLETED) {

            log.warn(
                    "Payment id: {} is already completed",
                    paymentId
            );

            throw new InvalidOperationException(
                    "Payment is already completed"
            );
        }

        if (payment.getPaymentStatus()
                == PaymentStatus.REFUNDED) {

            log.warn(
                    "Refunded payment id: {} cannot be completed",
                    paymentId
            );

            throw new InvalidOperationException(
                    "Refunded payment cannot be completed"
            );
        }

        Booking booking =
                payment.getBooking();

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

            log.warn(
                    "Payment id: {} cannot be completed because booking id: {} is cancelled",
                    paymentId,
                    booking.getId()
            );

            throw new InvalidOperationException(
                    "Payment cannot be completed for a cancelled booking"
            );
        }

        payment.setPaymentStatus(
                PaymentStatus.COMPLETED
        );

        payment.setPaymentDate(
                LocalDateTime.now()
        );

        payment.setTransactionId(
                UUID.randomUUID().toString()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        log.info(
                "Payment completed successfully with id: {}",
                savedPayment.getId()
        );

        notificationService.createNotification(
                savedPayment.getBooking().getUser(),
                savedPayment.getBooking().getEvent(),
                savedPayment.getBooking(),
                NotificationType.PAYMENT_COMPLETED,
                "Payment for booking "
                        + savedPayment.getBooking().getId()
                        + " has been completed"
        );

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    // REFUND PAYMENT
    @Override
    @Transactional
    public PaymentResponse refundPayment(
            Long paymentId
    ) {

        Payment payment =
                findPayment(paymentId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Payment refund requested for payment id: {} by user id: {}",
                paymentId,
                currentUser.getId()
        );

        validatePaymentAccess(
                payment,
                currentUser
        );

        if (payment.getPaymentStatus()
                == PaymentStatus.REFUNDED) {

            log.warn(
                    "Payment id: {} is already refunded",
                    paymentId
            );

            throw new InvalidOperationException(
                    "Payment is already refunded"
            );
        }

        if (payment.getPaymentStatus()
                != PaymentStatus.COMPLETED) {

            log.warn(
                    "Payment id: {} cannot be refunded because current status is: {}",
                    paymentId,
                    payment.getPaymentStatus()
            );

            throw new InvalidOperationException(
                    "Only completed payments can be refunded"
            );
        }

        payment.setPaymentStatus(
                PaymentStatus.REFUNDED
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        log.info(
                "Payment refunded successfully with id: {}",
                savedPayment.getId()
        );

        notificationService.createNotification(
                savedPayment.getBooking().getUser(),
                savedPayment.getBooking().getEvent(),
                savedPayment.getBooking(),
                NotificationType.PAYMENT_REFUNDED,
                "Payment for booking "
                        + savedPayment.getBooking().getId()
                        + " has been refunded"
        );

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    // --------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------

    private Payment findPayment(
            Long paymentId
    ) {

        return paymentRepository
                .findById(paymentId)
                .orElseThrow(() -> {

                    log.warn(
                            "Payment not found with id: {}",
                            paymentId
                    );

                    return new ResourceNotFoundException(
                            "Payment not found with id: "
                                    + paymentId
                    );
                });
    }

    private BigDecimal calculateAmount(
            Booking booking
    ) {

        if (booking.getBookingSeats() == null
                || booking.getBookingSeats().isEmpty()) {

            log.warn(
                    "Payment amount calculation failed because booking id: {} has no seats",
                    booking.getId()
            );

            throw new InvalidOperationException(
                    "Payment cannot be created because booking has no seats"
            );
        }

        return booking.getBookingSeats()
                .stream()
                .map(bookingSeat ->
                        bookingSeat
                                .getEventSeat()
                                .getPriceSeat()
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private void validateBookingOwnership(
            Booking booking,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        boolean isOwner =
                booking.getUser()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        if (!isAdmin && !isOwner) {

            log.warn(
                    "Unauthorized payment creation attempt. Booking id: {}, user id: {}",
                    booking.getId(),
                    currentUser.getId()
            );

            throw new InvalidOperationException(
                    "You are not allowed to make a payment for this booking"
            );
        }
    }

    private void validatePaymentAccess(
            Payment payment,
            User currentUser
    ) {

        boolean isAdmin =
                currentUser.getRole()
                        == Role.ADMIN;

        boolean isOwner =
                payment.getBooking()
                        .getUser()
                        .getId()
                        .equals(
                                currentUser.getId()
                        );

        if (!isAdmin && !isOwner) {

            log.warn(
                    "Unauthorized payment access attempt. Payment id: {}, user id: {}",
                    payment.getId(),
                    currentUser.getId()
            );

            throw new InvalidOperationException(
                    "You are not allowed to access or modify this payment"
            );
        }
    }
}