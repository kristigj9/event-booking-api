package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("user.repository@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Test
    void findByEmail_shouldReturnUserWhenEmailExists() {

        Optional<User> result =
                userRepository.findByEmail(
                        "user.repository@test.com"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "user.repository@test.com",
                result.get().getEmail()
        );

        assertEquals(
                Role.USER,
                result.get().getRole()
        );
    }

    @Test
    void findByEmail_shouldReturnEmptyWhenEmailDoesNotExist() {

        Optional<User> result =
                userRepository.findByEmail(
                        "notfound@test.com"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenEmailExists() {

        boolean result =
                userRepository.existsByEmail(
                        "user.repository@test.com"
                );

        assertTrue(result);
    }

    @Test
    void existsByEmail_shouldReturnFalseWhenEmailDoesNotExist() {

        boolean result =
                userRepository.existsByEmail(
                        "notfound@test.com"
                );

        assertFalse(result);
    }
}