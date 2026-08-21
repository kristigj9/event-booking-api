package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.auth.AuthResponse;
import com.lhind.event_booking_api.dto.auth.LoginRequest;
import com.lhind.event_booking_api.dto.auth.RegisterRequest;
import com.lhind.event_booking_api.entity.Role;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.UserMapper;
import com.lhind.event_booking_api.repository.UserRepository;
import com.lhind.event_booking_api.security.CustomUserDetailsService;
import com.lhind.event_booking_api.security.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log =
            LogManager.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    // REGISTER
    @Override
    @Transactional
    public AuthResponse register(
            RegisterRequest request
    ) {

        log.info(
                "Registration requested for email: {}",
                request.getEmail()
        );

        if (userRepository.existsByEmail(
                request.getEmail()
        )) {

            log.warn(
                    "Registration rejected because email already exists: {}",
                    request.getEmail()
            );

            throw new DuplicateResourceException(
                    "Email already exists: "
                            + request.getEmail()
            );
        }

        User user =
                userMapper.toEntity(request);

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // Regjistrimi publik krijon gjithmone USER
        user.setRole(
                Role.USER
        );

        User savedUser =
                userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        savedUser.getEmail()
                );

        String token =
                jwtService.generateToken(
                        userDetails
                );

        log.info(
                "User registered successfully with id: {}",
                savedUser.getId()
        );

        return AuthResponse.builder()
                .token(token)
                .user(
                        userMapper.toResponse(
                                savedUser
                        )
                )
                .build();
    }

    // LOGIN
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(
            LoginRequest request
    ) {

        log.info(
                "Login requested for email: {}",
                request.getEmail()
        );

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user =
                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "Authenticated user not found with email: {}",
                                    request.getEmail()
                            );

                            return new ResourceNotFoundException(
                                    "User not found with email: "
                                            + request.getEmail()
                            );
                        });

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        user.getEmail()
                );

        String token =
                jwtService.generateToken(
                        userDetails
                );

        log.info(
                "Login successful for user id: {}",
                user.getId()
        );

        return AuthResponse.builder()
                .token(token)
                .user(
                        userMapper.toResponse(
                                user)
                )
                .build();
    }
}