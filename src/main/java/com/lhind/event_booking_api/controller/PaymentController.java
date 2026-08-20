package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;
import com.lhind.event_booking_api.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(
        name = "Payments",
        description = "Payment management endpoints"
)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Create payment",
            description = "Creates a payment for an existing booking"
    )
    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> createPayment(
            @PathVariable Long bookingId,
            @Valid @RequestBody PaymentRequest request
    ) {

        PaymentResponse response =
                paymentService.createPayment(
                        bookingId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get payment by id",
            description = "Returns a payment by its id"
    )
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long paymentId
    ) {

        return ResponseEntity.ok(
                paymentService.getPaymentById(
                        paymentId
                )
        );
    }

    @Operation(
            summary = "Get payment by booking",
            description = "Returns the payment associated with a booking"
    )
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse> getPaymentByBookingId(
            @PathVariable Long bookingId
    ) {

        return ResponseEntity.ok(
                paymentService.getPaymentByBookingId(
                        bookingId
                )
        );
    }

    @Operation(
            summary = "Complete payment",
            description = "Changes a pending payment to completed"
    )
    @PatchMapping("/{paymentId}/complete")
    public ResponseEntity<PaymentResponse> completePayment(
            @PathVariable Long paymentId
    ) {

        return ResponseEntity.ok(
                paymentService.completePayment(
                        paymentId
                )
        );
    }

    @Operation(
            summary = "Refund payment",
            description = "Refunds a completed payment"
    )
    @PatchMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long paymentId
    ) {

        return ResponseEntity.ok(
                paymentService.refundPayment(
                        paymentId
                )
        );
    }
}