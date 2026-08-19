package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.user.ChangePasswordRequest;
import com.lhind.event_booking_api.dto.user.RoleUpdateRequest;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.dto.user.UserUpdateRequest;
import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {

        UserController userController =
                new UserController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        objectMapper = new ObjectMapper();

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void getCurrentUser_shouldReturnOk() throws Exception {

        when(userService.getCurrentUser())
                .thenReturn(userResponse);

        mockMvc.perform(
                        get("/api/users/me")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("user@test.com")
                )
                .andExpect(
                        jsonPath("$.role")
                                .value("USER")
                );

        verify(userService, times(1))
                .getCurrentUser();
    }

    @Test
    void updateCurrentUser_shouldReturnOk() throws Exception {

        UserUpdateRequest request =
                new UserUpdateRequest();

        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("updated@test.com");

        UserResponse updatedResponse =
                UserResponse.builder()
                        .id(1L)
                        .firstName("Updated")
                        .lastName("User")
                        .email("updated@test.com")
                        .role(Role.USER)
                        .build();

        when(userService.updateCurrentUser(
                any(UserUpdateRequest.class)
        )).thenReturn(updatedResponse);

        mockMvc.perform(
                        put("/api/users/me")
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
                        jsonPath("$.firstName")
                                .value("Updated")
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("updated@test.com")
                );

        verify(userService, times(1))
                .updateCurrentUser(
                        any(UserUpdateRequest.class)
                );
    }

    @Test
    void changePassword_shouldReturnNoContent() throws Exception {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        mockMvc.perform(
                        patch("/api/users/me/password")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isNoContent());

        verify(userService, times(1))
                .changePassword(
                        any(ChangePasswordRequest.class)
                );
    }

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(
                        List.of(userResponse)
                );

        mockMvc.perform(
                        get("/api/users")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].email")
                                .value("user@test.com")
                );

        verify(userService, times(1))
                .getAllUsers();
    }

    @Test
    void getUserById_shouldReturnOk() throws Exception {

        when(userService.getUserById(1L))
                .thenReturn(userResponse);

        mockMvc.perform(
                        get("/api/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("user@test.com")
                );

        verify(userService, times(1))
                .getUserById(1L);
    }

    @Test
    void updateUserRole_shouldReturnOk() throws Exception {

        RoleUpdateRequest request =
                new RoleUpdateRequest();

        request.setRole(
                Role.ORGANIZER
        );

        UserResponse organizerResponse =
                UserResponse.builder()
                        .id(1L)
                        .firstName("Test")
                        .lastName("User")
                        .email("user@test.com")
                        .role(Role.ORGANIZER)
                        .build();

        when(userService.updateUserRole(
                eq(1L),
                any(RoleUpdateRequest.class)
        )).thenReturn(organizerResponse);

        mockMvc.perform(
                        patch("/api/users/1/role")
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
                        jsonPath("$.role")
                                .value("ORGANIZER")
                );

        verify(userService, times(1))
                .updateUserRole(
                        eq(1L),
                        any(RoleUpdateRequest.class)
                );
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {

        mockMvc.perform(
                        delete("/api/users/1")
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(userService, times(1))
                .deleteUser(1L);
    }
//    VALIDATION TEST

    @Test
    void updateCurrentUser_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        UserUpdateRequest request =
                new UserUpdateRequest();

        request.setFirstName("");
        request.setLastName("");
        request.setEmail("invalid-email");

        mockMvc.perform(
                        put("/api/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .updateCurrentUser(any(UserUpdateRequest.class));
    }

    @Test
    void changePassword_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("");
        request.setNewPassword("");
        request.setConfirmPassword("");

        mockMvc.perform(
                        patch("/api/users/me/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .changePassword(any(ChangePasswordRequest.class));
    }

    @Test
    void updateUserRole_shouldReturnBadRequestWhenRoleIsMissing()
            throws Exception {

        RoleUpdateRequest request =
                new RoleUpdateRequest();

        request.setRole(null);

        mockMvc.perform(
                        patch("/api/users/1/role")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());

        verify(userService, never())
                .updateUserRole(
                        eq(1L),
                        any(RoleUpdateRequest.class)
                );
    }
}