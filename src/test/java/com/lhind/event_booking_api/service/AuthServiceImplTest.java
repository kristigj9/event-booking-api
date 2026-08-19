package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.auth.AuthResponse;
import com.lhind.event_booking_api.dto.auth.LoginRequest;
import com.lhind.event_booking_api.dto.auth.RegisterRequest;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.UserMapper;
import com.lhind.event_booking_api.repository.UserRepository;
import com.lhind.event_booking_api.security.CustomUserDetailsService;
import com.lhind.event_booking_api.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private UserResponse userResponse;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setEmail("user@test.com");
        registerRequest.setPassword("Test12345");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("Test12345");

        user = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_shouldRegisterUserSuccessfully() {

        when(userRepository.existsByEmail("user@test.com"))
                .thenReturn(false);

        when(userMapper.toEntity(registerRequest))
                .thenReturn(user);

        when(passwordEncoder.encode("Test12345"))
                .thenReturn("encoded-password");

        when(userRepository.save(user))
                .thenReturn(user);

        when(userDetailsService.loadUserByUsername(
                "user@test.com"
        )).thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-jwt-token");

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        AuthResponse result =
                authService.register(registerRequest);

        assertNotNull(result);

        assertEquals(
                "test-jwt-token",
                result.getToken()
        );

        assertEquals(
                Role.USER,
                user.getRole()
        );

        assertEquals(
                "encoded-password",
                user.getPassword()
        );

        assertEquals(
                userResponse,
                result.getUser()
        );

        verify(passwordEncoder, times(1))
                .encode("Test12345");

        verify(userRepository, times(1))
                .save(user);

        verify(jwtService, times(1))
                .generateToken(userDetails);
    }

    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail("user@test.com"))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> authService.register(registerRequest)
                );

        assertEquals(
                "Email already exists: user@test.com",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(jwtService, never())
                .generateToken(any(UserDetails.class));
    }

    @Test
    void login_shouldLoginSuccessfully() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(userDetailsService.loadUserByUsername(
                "user@test.com"
        )).thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-jwt-token");

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        AuthResponse result =
                authService.login(loginRequest);

        assertNotNull(result);

        assertEquals(
                "test-jwt-token",
                result.getToken()
        );

        assertEquals(
                userResponse,
                result.getUser()
        );

        verify(authenticationManager, times(1))
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(jwtService, times(1))
                .generateToken(userDetails);
    }

    @Test
    void login_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> authService.login(loginRequest)
                );

        assertEquals(
                "User not found with email: user@test.com",
                exception.getMessage()
        );

        verify(authenticationManager, times(1))
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(jwtService, never())
                .generateToken(any(UserDetails.class));
    }
}