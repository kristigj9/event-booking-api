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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

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

        return userMapper.toResponse(user);
    }

    // GET USER BY ID - ADMIN
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = findUser(id);

        return userMapper.toResponse(user);
    }

    // GET ALL USERS - ADMIN
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

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

        /*
         * Kontrollojme duplicate email vetem
         * nese user po ndryshon email
         */
        if (!user.getEmail()
                .equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(
                request.getEmail()
        )) {

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

        // Kontrollojm current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )) {

            throw new InvalidPasswordException(
                    "Current password is incorrect"
            );
        }

        // Kontrollojm newPassword + confirmPassword
        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new PasswordMismatchException(
                    "New password and confirmation do not match"
            );
        }

        /*
         * Opsionale, por e rekomanduar:
         * password-i i ri nuk duhet te jete
         * i njejte me te vjetrin.
         */
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword()
        )) {

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
    }

    // DELETE USER - ADMIN
    @Override
    @Transactional
    public void deleteUser(Long id) {

        User user = findUser(id);

        userRepository.delete(user);
    }

    // PRIVATE HELPER METHODS

    // ADMIN - ndryshon rolin e nje user-i
    @Override
    @Transactional
    public UserResponse updateUserRole(
            Long id,
            RoleUpdateRequest request
    ) {

        User user = findUser(id);

        user.setRole(request.getRole());

        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    private User findUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }
}