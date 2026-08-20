package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;
import com.lhind.event_booking_api.entity.*;
import com.lhind.event_booking_api.exception.InvalidOperationException;
import com.lhind.event_booking_api.mapper.PaymentMapper;
import com.lhind.event_booking_api.repository.BookingRepository;
import com.lhind.event_booking_api.repository.PaymentRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private User user;
    private Booking booking;
    private PaymentRequest paymentRequest;
    private Event event;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .role(Role.USER)
                .build();

        event = Event.builder()
                .id(1L)
                .eventName("Test Event")
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        EventSeat eventSeat1 = EventSeat.builder()
                .id(1L)
                .priceSeat(new BigDecimal("10.00"))
                .build();

        EventSeat eventSeat2 = EventSeat.builder()
                .id(2L)
                .priceSeat(new BigDecimal("15.00"))
                .build();

        booking = Booking.builder()
                .id(1L)
                .user(user)
                .event(event)
                .bookingStatus(BookingStatus.PENDING)
                .bookingSeats(new ArrayList<>())
                .build();

        BookingSeat bookingSeat1 = BookingSeat.builder()
                .id(1L)
                .booking(booking)
                .eventSeat(eventSeat1)
                .build();

        BookingSeat bookingSeat2 = BookingSeat.builder()
                .id(2L)
                .booking(booking)
                .eventSeat(eventSeat2)
                .build();

        booking.getBookingSeats().add(bookingSeat1);
        booking.getBookingSeats().add(bookingSeat2);

        paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentMethod(PaymentMethod.CARD);
    }

    // CREATE PAYMENT

    @Test
    void createPayment_shouldCalculateAmountFromEventSeats() {

        Payment payment = Payment.builder()
                .paymentMethod(PaymentMethod.CARD)
                .build();

        Payment savedPayment = Payment.builder()
                .id(1L)
                .booking(booking)
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(new BigDecimal("25.00"))
                .build();

        PaymentResponse response = PaymentResponse.builder()
                .id(1L)
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(paymentRepository.existsByBookingId(1L))
                .thenReturn(false);

        when(paymentMapper.toEntity(paymentRequest))
                .thenReturn(payment);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(savedPayment);

        when(paymentMapper.toResponse(savedPayment))
                .thenReturn(response);

        PaymentResponse result =
                paymentService.createPayment(
                        1L,
                        paymentRequest
                );

        assertNotNull(result);

        assertEquals(
                new BigDecimal("25.00"),
                result.getAmount()
        );

        assertEquals(
                PaymentStatus.PENDING,
                result.getPaymentStatus()
        );

        verify(paymentRepository)
                .save(argThat(saved ->
                        saved.getAmount()
                                .compareTo(
                                        new BigDecimal("25.00")
                                ) == 0
                                && saved.getPaymentStatus()
                                == PaymentStatus.PENDING
                                && saved.getBooking()
                                == booking
                ));
    }

    @Test
    void createPayment_shouldThrowExceptionWhenPaymentAlreadyExists() {

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(paymentRepository.existsByBookingId(1L))
                .thenReturn(true);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () ->
                                paymentService.createPayment(
                                        1L,
                                        paymentRequest
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("Payment already exists")
        );

        verify(paymentRepository, never())
                .save(any());
    }

    @Test
    void createPayment_shouldThrowExceptionForCancelledBooking() {

        booking.setBookingStatus(
                BookingStatus.CANCELLED
        );

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(paymentRepository.existsByBookingId(1L))
                .thenReturn(false);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () ->
                                paymentService.createPayment(
                                        1L,
                                        paymentRequest
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("cancelled booking")
        );

        verify(paymentRepository, never())
                .save(any());
    }

    // AUTHORIZATION

    @Test
    void createPayment_shouldThrowExceptionWhenUserIsNotBookingOwner() {

        User anotherUser = User.builder()
                .id(2L)
                .role(Role.USER)
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(anotherUser);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () ->
                                paymentService.createPayment(
                                        1L,
                                        paymentRequest
                                )
                );

        assertTrue(
                exception.getMessage()
                        .contains("not allowed")
        );

        verify(paymentRepository, never())
                .save(any());
    }

    // COMPLETE PAYMENT

    @Test
    void completePayment_shouldSetStatusDateAndTransactionId() {

        Payment payment = Payment.builder()
                .id(1L)
                .booking(booking)
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenAnswer(invocation -> {

                    Payment p =
                            invocation.getArgument(0);

                    return PaymentResponse.builder()
                            .id(p.getId())
                            .amount(p.getAmount())
                            .paymentStatus(
                                    p.getPaymentStatus()
                            )
                            .paymentMethod(
                                    p.getPaymentMethod()
                            )
                            .paymentDate(
                                    p.getPaymentDate()
                            )
                            .transactionId(
                                    p.getTransactionId()
                            )
                            .build();
                });

        PaymentResponse result =
                paymentService.completePayment(1L);

        assertEquals(
                PaymentStatus.COMPLETED,
                result.getPaymentStatus()
        );

        assertNotNull(
                result.getPaymentDate()
        );

        assertNotNull(
                result.getTransactionId()
        );

        verify(paymentRepository)
                .save(payment);

        verify(notificationService, times(1))
                .createNotification(
                        eq(user),
                        eq(event),
                        eq(booking),
                        eq(NotificationType.PAYMENT_COMPLETED),
                        anyString()
                );
    }

    @Test
    void completePayment_shouldThrowExceptionWhenAlreadyCompleted() {

        Payment payment = Payment.builder()
                .id(1L)
                .booking(booking)
                .paymentStatus(
                        PaymentStatus.COMPLETED
                )
                .build();

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        assertThrows(
                InvalidOperationException.class,
                () ->
                        paymentService.completePayment(1L)
        );

        verify(paymentRepository, never())
                .save(any());

        verify(notificationService, never())
                .createNotification(
                        any(),
                        any(),
                        any(),
                        any(),
                        anyString()
                );
    }

    // REFUND

    @Test
    void refundPayment_shouldChangeCompletedPaymentToRefunded() {

        Payment payment = Payment.builder()
                .id(1L)
                .booking(booking)
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(
                        PaymentStatus.COMPLETED
                )
                .build();

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(paymentRepository.save(payment))
                .thenReturn(payment);

        when(paymentMapper.toResponse(payment))
                .thenAnswer(invocation -> {

                    Payment p =
                            invocation.getArgument(0);

                    return PaymentResponse.builder()
                            .id(p.getId())
                            .amount(p.getAmount())
                            .paymentStatus(
                                    p.getPaymentStatus()
                            )
                            .build();
                });

        PaymentResponse result =
                paymentService.refundPayment(1L);

        assertEquals(
                PaymentStatus.REFUNDED,
                result.getPaymentStatus()
        );

        verify(paymentRepository)
                .save(payment);

        verify(notificationService, times(1))
                .createNotification(
                        eq(user),
                        eq(event),
                        eq(booking),
                        eq(NotificationType.PAYMENT_REFUNDED),
                        anyString()
                );
    }

    @Test
    void refundPayment_shouldThrowExceptionWhenPaymentIsPending() {

        Payment payment = Payment.builder()
                .id(1L)
                .booking(booking)
                .paymentStatus(
                        PaymentStatus.PENDING
                )
                .build();

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        InvalidOperationException exception =
                assertThrows(
                        InvalidOperationException.class,
                        () ->
                                paymentService.refundPayment(1L)
                );

        assertTrue(
                exception.getMessage()
                        .contains(
                                "Only completed payments"
                        )
        );

        verify(paymentRepository, never())
                .save(any());

        verify(notificationService, never())
                .createNotification(
                        any(),
                        any(),
                        any(),
                        any(),
                        anyString()
                );
    }
}