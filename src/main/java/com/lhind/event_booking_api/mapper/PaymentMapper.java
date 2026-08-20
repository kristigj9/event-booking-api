package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.booking.BookingResponseShort;
import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;
import com.lhind.event_booking_api.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequest request) {
        if (request == null) {
            return null;
        }

        return Payment.builder()
                .paymentMethod(request.getPaymentMethod())
                .build();
    }

    public PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        BookingResponseShort bookingResponse = null;

        if (payment.getBooking() != null) {
            bookingResponse = BookingResponseShort.builder()
                    .id(payment.getBooking().getId())
                    .status(payment.getBooking().getBookingStatus())
                    .build();
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .paymentDate(payment.getPaymentDate())
                .booking(bookingResponse)
                .build();
    }
}