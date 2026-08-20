package com.lhind.event_booking_api.dto.payment;

import com.lhind.event_booking_api.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    @NotNull
    private PaymentMethod paymentMethod;
}