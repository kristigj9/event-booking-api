package com.lhind.event_booking_api.dto.payment;

import com.lhind.event_booking_api.dto.reference.BookingResponseShort;
import com.lhind.event_booking_api.entity.PaymentMethod;
import com.lhind.event_booking_api.entity.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PaymentResponse {

    private Long id;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionId;

    private LocalDateTime paymentDate;

    private BookingResponseShort booking;
}