package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.user.ChangePasswordRequest;
import com.lhind.event_booking_api.dto.user.RoleUpdateRequest;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.dto.user.UserUpdateRequest;
import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidPasswordException;
import com.lhind.event_booking_api.exception.PasswordMismatchException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.UserMapper;
import com.lhind.event_booking_api.repository.UserRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse response;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        response = UserResponse.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.USER)
                .build();
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUserSuccessfully() {

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.getCurrentUser();

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void getUserById_shouldReturnUserSuccessfully() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.getUserById(99L)
                );

        assertEquals(
                "User not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {

        List<User> users =
                List.of(user);

        List<UserResponse> responses =
                List.of(response);

        when(userRepository.findAll())
                .thenReturn(users);

        when(userMapper.toResponseList(users))
                .thenReturn(responses);

        List<UserResponse> result =
                userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void updateCurrentUser_shouldUpdateSuccessfully() {

        UserUpdateRequest request =
                new UserUpdateRequest();

        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("user@test.com");

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.updateCurrentUser(request);

        assertNotNull(result);

        verify(userMapper, times(1))
                .updateEntity(
                        request,
                        user
                );

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void updateCurrentUser_shouldThrowExceptionWhenEmailAlreadyExists() {

        UserUpdateRequest request =
                new UserUpdateRequest();

        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("existing@test.com");

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(userRepository.existsByEmail(
                "existing@test.com"
        )).thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> userService.updateCurrentUser(request)
                );

        assertEquals(
                "Email already exists",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

//    TESTET E PASSWORD

    @Test
    void changePassword_shouldChangePasswordSuccessfully() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.matches(
                "OldPassword123",
                "encoded-password"
        )).thenReturn(true);

        when(passwordEncoder.matches(
                "NewPassword123",
                "encoded-password"
        )).thenReturn(false);

        when(passwordEncoder.encode(
                "NewPassword123"
        )).thenReturn("new-encoded-password");

        userService.changePassword(request);

        assertEquals(
                "new-encoded-password",
                user.getPassword()
        );

        verify(passwordEncoder, times(1))
                .encode("NewPassword123");

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void changePassword_shouldThrowExceptionWhenCurrentPasswordIsIncorrect() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("WrongPassword");
        request.setNewPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.matches(
                "WrongPassword",
                "encoded-password"
        )).thenReturn(false);

        InvalidPasswordException exception =
                assertThrows(
                        InvalidPasswordException.class,
                        () -> userService.changePassword(request)
                );

        assertEquals(
                "Current password is incorrect",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenPasswordsDoNotMatch() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");
        request.setConfirmPassword("DifferentPassword123");

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.matches(
                "OldPassword123",
                "encoded-password"
        )).thenReturn(true);

        PasswordMismatchException exception =
                assertThrows(
                        PasswordMismatchException.class,
                        () -> userService.changePassword(request)
                );

        assertEquals(
                "New password and confirmation do not match",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void changePassword_shouldThrowExceptionWhenNewPasswordIsSameAsCurrentPassword() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("OldPassword123");
        request.setConfirmPassword("OldPassword123");

        when(authenticatedUserService.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.matches(
                "OldPassword123",
                "encoded-password"
        ))
                .thenReturn(true)
                .thenReturn(true);

        InvalidPasswordException exception =
                assertThrows(
                        InvalidPasswordException.class,
                        () -> userService.changePassword(request)
                );

        assertEquals(
                "New password must be different from current password",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserRole_shouldUpdateRoleSuccessfully() {

        RoleUpdateRequest request =
                new RoleUpdateRequest();

        request.setRole(Role.ORGANIZER);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        response.setRole(Role.ORGANIZER);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.updateUserRole(
                        1L,
                        request
                );

        assertNotNull(result);

        assertEquals(
                Role.ORGANIZER,
                user.getRole()
        );

        verify(userRepository, times(1))
                .save(user);
    }

    @Test
    void deleteUser_shouldDeleteUserSuccessfully() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1))
                .delete(user);
    }
    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> userService.deleteUser(99L)
                );

        assertEquals(
                "User not found with id: 99",
                exception.getMessage()
        );

        verify(userRepository, never())
                .delete(any(User.class));
    }
}
