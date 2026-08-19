package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.user.ChangePasswordRequest;
import com.lhind.event_booking_api.dto.user.RoleUpdateRequest;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.dto.user.UserUpdateRequest;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.InvalidPasswordException;
import com.lhind.event_booking_api.exception.PasswordMismatchException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.UserMapper;
import com.lhind.event_booking_api.repository.UserRepository;
import com.lhind.event_booking_api.security.AuthenticatedUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log =
            LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticatedUserService authenticatedUserService;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticatedUserService authenticatedUserService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticatedUserService = authenticatedUserService;
    }

    // GET CURRENT USER
    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {

        User user =
                authenticatedUserService.getCurrentUser();

        log.debug(
                "Fetching current user with id: {}",
                user.getId()
        );

        return userMapper.toResponse(user);
    }

    // GET USER BY ID - ADMIN
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        log.debug(
                "Fetching user by id: {}",
                id
        );

        User user = findUser(id);

        return userMapper.toResponse(user);
    }

    // GET ALL USERS - ADMIN
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        log.debug(
                "Fetching all users"
        );

        List<User> users =
                userRepository.findAll();

        return userMapper.toResponseList(users);
    }

    // UPDATE CURRENT USER
    @Override
    @Transactional
    public UserResponse updateCurrentUser(
            UserUpdateRequest request
    ) {

        User user =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Profile update requested for user id: {}",
                user.getId()
        );

        if (!user.getEmail()
                .equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(
                request.getEmail()
        )) {

            log.warn(
                    "Profile update rejected for user id: {} because email already exists: {}",
                    user.getId(),
                    request.getEmail()
            );

            throw new DuplicateResourceException(
                    "Email already exists"
            );
        }

        userMapper.updateEntity(
                request,
                user
        );

        User updatedUser =
                userRepository.save(user);

        log.info(
                "User profile updated successfully with id: {}",
                updatedUser.getId()
        );

        return userMapper.toResponse(updatedUser);
    }

    // CHANGE PASSWORD
    @Override
    @Transactional
    public void changePassword(
            ChangePasswordRequest request
    ) {

        User user =
                authenticatedUserService.getCurrentUser();

        log.info(
                "Password change requested for user id: {}",
                user.getId()
        );

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {

            log.warn(
                    "Password change rejected for user id: {} because current password is incorrect",
                    user.getId()
            );

            throw new InvalidPasswordException(
                    "Current password is incorrect"
            );
        }

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            log.warn(
                    "Password change rejected for user id: {} because confirmation does not match",
                    user.getId()
            );

            throw new PasswordMismatchException(
                    "New password and confirmation do not match"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword()
        )) {

            log.warn(
                    "Password change rejected for user id: {} because new password matches current password",
                    user.getId()
            );

            throw new InvalidPasswordException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        log.info(
                "Password changed successfully for user id: {}",
                user.getId()
        );
    }

    // DELETE USER - ADMIN
    @Override
    @Transactional
    public void deleteUser(Long id) {

        log.info(
                "Delete requested for user id: {}",
                id
        );

        User user = findUser(id);

        userRepository.delete(user);

        log.info(
                "User deleted successfully with id: {}",
                id
        );
    }

    // ADMIN - ndryshon rolin e nje user-i
    @Override
    @Transactional
    public UserResponse updateUserRole(
            Long id,
            RoleUpdateRequest request
    ) {

        log.info(
                "Role update requested for user id: {}. New role: {}",
                id,
                request.getRole()
        );

        User user = findUser(id);

        user.setRole(request.getRole());

        User updatedUser =
                userRepository.save(user);

        log.info(
                "User role updated successfully for user id: {} to role: {}",
                updatedUser.getId(),
                updatedUser.getRole()
        );

        return userMapper.toResponse(updatedUser);
    }

    // PRIVATE HELPER METHOD
    private User findUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "User not found with id: {}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "User not found with id: " + id
                    );
                });
    }
}