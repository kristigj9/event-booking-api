package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.auth.AuthResponse;
import com.lhind.event_booking_api.dto.auth.LoginRequest;
import com.lhind.event_booking_api.dto.auth.RegisterRequest;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        AuthController authController =
                new AuthController(authService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();

        objectMapper =
                new ObjectMapper();
    }

    @Test
    void register_shouldReturnCreated() throws Exception {

        RegisterRequest request =
                new RegisterRequest();

        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("user@test.com");
        request.setPassword("Test12345");

        UserResponse userResponse =
                UserResponse.builder()
                        .id(1L)
                        .firstName("Test")
                        .lastName("User")
                        .email("user@test.com")
                        .role(Role.USER)
                        .build();

        AuthResponse authResponse =
                AuthResponse.builder()
                        .token("test-token")
                        .user(userResponse)
                        .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.token")
                                .value("test-token")
                )
                .andExpect(
                        jsonPath("$.user.email")
                                .value("user@test.com")
                )
                .andExpect(
                        jsonPath("$.user.role")
                                .value("USER")
                );

        verify(authService)
                .register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnOk() throws Exception {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("user@test.com");
        request.setPassword("Test12345");

        UserResponse userResponse =
                UserResponse.builder()
                        .id(1L)
                        .firstName("Test")
                        .lastName("User")
                        .email("user@test.com")
                        .role(Role.USER)
                        .build();

        AuthResponse authResponse =
                AuthResponse.builder()
                        .token("test-token")
                        .user(userResponse)
                        .build();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(authResponse);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.token")
                                .value("test-token")
                )
                .andExpect(
                        jsonPath("$.user.email")
                                .value("user@test.com")
                );

        verify(authService)
                .login(any(LoginRequest.class));
    }

//    VALIDATION TEST

    @Test
    void register_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        RegisterRequest request =
                new RegisterRequest();

        request.setFirstName("");
        request.setLastName("");
        request.setEmail("invalid-email");
        request.setPassword("");

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .register(any(RegisterRequest.class));

    }

//    LOGIN TEST

    @Test
    void login_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("invalid-email");
        request.setPassword("");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(authService, never())
                .login(any(LoginRequest.class));
    }


}