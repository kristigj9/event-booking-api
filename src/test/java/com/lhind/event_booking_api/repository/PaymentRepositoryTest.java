package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;


    @Test
    void findByBookingId_shouldReturnPayment() {

        User user = createAndSaveUser(
                "payment-test-1@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Venue 1"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Event 1"
        );

        Booking booking = createAndSaveBooking(
                user,
                event,
                BookingStatus.PENDING
        );

        Payment payment = Payment.builder()
                .amount(new BigDecimal("25.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .booking(booking)
                .build();

        paymentRepository.save(payment);

        Optional<Payment> result =
                paymentRepository.findByBookingId(
                        booking.getId()
                );

        assertTrue(result.isPresent());

        assertEquals(
                new BigDecimal("25.00"),
                result.get().getAmount()
        );

        assertEquals(
                PaymentMethod.CARD,
                result.get().getPaymentMethod()
        );

        assertEquals(
                PaymentStatus.PENDING,
                result.get().getPaymentStatus()
        );

        assertEquals(
                booking.getId(),
                result.get()
                        .getBooking()
                        .getId()
        );
    }


    @Test
    void existsByBookingId_shouldReturnTrueWhenPaymentExists() {

        User user = createAndSaveUser(
                "payment-test-2@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Venue 2"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Event 2"
        );

        Booking booking = createAndSaveBooking(
                user,
                event,
                BookingStatus.PENDING
        );

        Payment payment = Payment.builder()
                .amount(new BigDecimal("40.00"))
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .booking(booking)
                .build();

        paymentRepository.save(payment);

        boolean exists =
                paymentRepository.existsByBookingId(
                        booking.getId()
                );

        assertTrue(exists);
    }


    @Test
    void existsByBookingId_shouldReturnFalseWhenPaymentDoesNotExist() {

        User user = createAndSaveUser(
                "payment-test-3@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Venue 3"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Event 3"
        );

        Booking booking = createAndSaveBooking(
                user,
                event,
                BookingStatus.PENDING
        );

        boolean exists =
                paymentRepository.existsByBookingId(
                        booking.getId()
                );

        assertFalse(exists);
    }


    @Test
    void findByTransactionId_shouldReturnPayment() {

        User user = createAndSaveUser(
                "payment-test-4@test.com"
        );

        Venue venue = createAndSaveVenue(
                "Venue 4"
        );

        Event event = createAndSaveEvent(
                user,
                venue,
                "Event 4"
        );

        Booking booking = createAndSaveBooking(
                user,
                event,
                BookingStatus.CONFIRMED
        );

        String transactionId =
                "TXN-TEST-001";

        Payment payment = Payment.builder()
                .amount(new BigDecimal("50.00"))
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.COMPLETED)
                .transactionId(transactionId)
                .paymentDate(LocalDateTime.now())
                .booking(booking)
                .build();

        paymentRepository.save(payment);

        Optional<Payment> result =
                paymentRepository.findByTransactionId(
                        transactionId
                );

        assertTrue(result.isPresent());

        assertEquals(
                transactionId,
                result.get().getTransactionId()
        );

        assertEquals(
                PaymentStatus.COMPLETED,
                result.get().getPaymentStatus()
        );

        assertNotNull(
                result.get().getPaymentDate()
        );
    }


    @Test
    void findByTransactionId_shouldReturnEmptyWhenTransactionDoesNotExist() {

        Optional<Payment> result =
                paymentRepository.findByTransactionId(
                        "NON-EXISTING-TRANSACTION"
                );

        assertTrue(result.isEmpty());
    }


    // --------------------------------
    // PRIVATE HELPER METHODS
    // --------------------------------

    private User createAndSaveUser(
            String email
    ) {

        User user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .password("password")
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }


    private Venue createAndSaveVenue(
            String venueName
    ) {

        Venue venue = Venue.builder()
                .venueName(venueName)
                .venueAddress("Test Address")
                .venueCity("Tirana")
                .venueCapacity(100)
                .build();

        return venueRepository.save(venue);
    }


    private Event createAndSaveEvent(
            User organizer,
            Venue venue,
            String eventName
    ) {

        Event event = Event.builder()
                .eventName(eventName)
                .eventDescription("Test Description")
                .eventStartDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                )
                .eventEndDateTime(
                        LocalDateTime.now()
                                .plusDays(1)
                                .plusHours(2)
                )
                .eventTotalSeats(100)
                .eventAvailableSeats(100)
                .venue(venue)
                .organizer(organizer)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        return eventRepository.save(event);
    }


    private Booking createAndSaveBooking(
            User user,
            Event event,
            BookingStatus status
    ) {

        Booking booking = Booking.builder()
                .seatsBooked(1)
                .bookingStatus(status)
                .user(user)
                .event(event)
                .build();

        return bookingRepository.save(booking);
    }
}