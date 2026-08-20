package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);
}