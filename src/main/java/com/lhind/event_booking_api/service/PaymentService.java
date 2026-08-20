package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(Long bookingId, PaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    PaymentResponse getPaymentByBookingId(Long bookingId);

    PaymentResponse completePayment(Long paymentId);

    PaymentResponse refundPayment(Long paymentId);
}