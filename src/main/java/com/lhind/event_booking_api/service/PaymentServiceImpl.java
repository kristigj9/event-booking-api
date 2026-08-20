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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    @Transactional
    public PaymentResponse createPayment(
            Long bookingId,
            PaymentRequest request
    ) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found with id: " + bookingId
                        )
                );

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validateBookingOwnership(
                booking,
                currentUser
        );

        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new InvalidOperationException(
                    "Payment already exists for booking: " + bookingId
            );
        }

        if (booking.getBookingStatus()
                == BookingStatus.CANCELLED) {

            throw new InvalidOperationException(
                    "Payment cannot be created for a cancelled booking"
            );
        }

        if (booking.getBookingStatus()
                == BookingStatus.COMPLETED) {

            throw new InvalidOperationException(
                    "Payment cannot be created for a completed booking"
            );
        }

        Payment payment =
                paymentMapper.toEntity(request);

        payment.setBooking(booking);
        payment.setAmount(calculateAmount(booking));
        payment.setPaymentStatus(
                PaymentStatus.PENDING
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            Long paymentId
    ) {

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

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(
            Long bookingId
    ) {

        Payment payment =
                paymentRepository
                        .findByBookingId(bookingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found for booking: "
                                                + bookingId
                                )
                        );

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

    @Override
    @Transactional
    public PaymentResponse completePayment(
            Long paymentId
    ) {

        Payment payment =
                findPayment(paymentId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validatePaymentAccess(
                payment,
                currentUser
        );

        if (payment.getPaymentStatus()
                == PaymentStatus.COMPLETED) {

            throw new InvalidOperationException(
                    "Payment is already completed"
            );
        }

        if (payment.getPaymentStatus()
                == PaymentStatus.REFUNDED) {

            throw new InvalidOperationException(
                    "Refunded payment cannot be completed"
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

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(
            Long paymentId
    ) {

        Payment payment =
                findPayment(paymentId);

        User currentUser =
                authenticatedUserService.getCurrentUser();

        validatePaymentAccess(
                payment,
                currentUser
        );

        if (payment.getPaymentStatus()
                != PaymentStatus.COMPLETED) {

            throw new InvalidOperationException(
                    "Only completed payments can be refunded"
            );
        }

        payment.setPaymentStatus(
                PaymentStatus.REFUNDED
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        return paymentMapper.toResponse(
                savedPayment
        );
    }

    // PRIVATE HELPER METHODS

    private Payment findPayment(
            Long paymentId
    ) {

        return paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: "
                                        + paymentId
                        )
                );
    }

    private BigDecimal calculateAmount(
            Booking booking
    ) {

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

            throw new InvalidOperationException(
                    "You are not allowed to access or modify this payment"
            );
        }
    }
}