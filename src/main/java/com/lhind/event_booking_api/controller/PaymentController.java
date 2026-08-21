package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.payment.PaymentRequest;
import com.lhind.event_booking_api.dto.payment.PaymentResponse;
import com.lhind.event_booking_api.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
        description = "Endpoints for creating, retrieving, completing and refunding payments"
)
public class PaymentController {

    private final PaymentService paymentService;

    // BOOKING OWNER / ADMIN
    @Operation(
            summary = "Create payment",
            description = "Creates a payment for an existing booking. Accessible by the booking owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Payment created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment already exists or booking cannot be paid in its current state"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found"
            )
    })
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

    // PAYMENT OWNER / ADMIN
    @Operation(
            summary = "Get payment by id",
            description = "Returns a payment by its id. Accessible by the booking owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
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

    // BOOKING OWNER / ADMIN
    @Operation(
            summary = "Get payment by booking",
            description = "Returns the payment associated with a booking. Accessible by the booking owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found for booking"
            )
    })
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

    // PAYMENT OWNER / ADMIN
    @Operation(
            summary = "Complete payment",
            description = "Changes a payment to COMPLETED and generates a transaction id. Accessible by the booking owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment is already completed or cannot be completed in its current state"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
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

    // PAYMENT OWNER / ADMIN
    @Operation(
            summary = "Refund payment",
            description = "Refunds a COMPLETED payment. Accessible by the booking owner or ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment refunded successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Payment is already refunded or is not completed"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"
            )
    })
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