package com.lhind.event_booking_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomAuthenticationEntryPoint authenticationEntryPoint,
            CustomAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // PASSWORD ENCODER
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // AUTHENTICATION MANAGER
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) {

        return configuration.getAuthenticationManager();
    }

    // SECURITY CONFIGURATION
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // JWT API nuk perdor CSRF token
                .csrf(AbstractHttpConfigurer::disable)

                // JWT API nuk perdor session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // 401 / 403 custom JSON response
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        authenticationEntryPoint
                                )
                                .accessDeniedHandler(
                                        accessDeniedHandler
                                )
                )

                .authorizeHttpRequests(auth -> auth

                        // SWAGGER / OPENAPI - PUBLIC

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // AUTH - PUBLIC

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // EVENTS

                        // PUBLIC
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/events/**"
                        ).permitAll()

                        // ORGANIZER / ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/events"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER owner / ADMIN
                        // Ownership kontrollohet  Service
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/events/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER owner / ADMIN
                        // Ownership kontrollohet Service
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/events/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // USERS - CURRENT USER

                        // USER / ORGANIZER / ADMIN
                        .requestMatchers(
                                "/api/users/me",
                                "/api/users/me/**"
                        ).authenticated()

                        // USERS - ADMIN
                        //

                        .requestMatchers(
                                "/api/users/**"
                        ).hasRole("ADMIN")


                        // VENUES


                        // PUBLIC - vetem lexim
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/venues/**"
                        ).permitAll()

                        // ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/venues/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/venues/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/venues/**"
                        ).hasRole("ADMIN")


                        // CATEGORIES


                        // PUBLIC - vetem lexim
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories/**"
                        ).permitAll()

                        // ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        // SEATS

                        // PUBLIC - vetem lexim
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/seats/**"
                        ).permitAll()

                        // ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/seats/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/seats/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/seats/**"
                        ).hasRole("ADMIN")

                        // EVENT SEATS

                        // PUBLIC - vetem lexim
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/event-seats/**"
                        ).permitAll()

                        // ORGANIZER owner / ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/event-seats/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/event-seats/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/event-seats/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // BOOKINGS

                        // USER / ORGANIZER / ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/bookings"
                        ).authenticated()

                        // Booking-et personale
                        // Duhet te jene para /api/bookings/*
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/me",
                                "/api/bookings/me/status/*"
                        ).authenticated()

                        // ORGANIZER owner / ADMIN
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/event/*"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // PENDING -> CONFIRMED
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/bookings/*/confirm"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // CONFIRMED -> COMPLETED
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/bookings/*/complete"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // USER owner / ADMIN
                        // Ownership kontrollohet  Service
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/bookings/*/cancel"
                        ).authenticated()

                        // USER owner / ADMIN
                        // Ownership kontrollohet  Service
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/*"
                        ).authenticated()

                        // PAYMENTS

                        // BOOKING OWNER / ADMIN
                        // Ownership kontrollohet  Service
                        .requestMatchers(
                                "/api/payments/**"
                        ).authenticated()

                        // REVIEWS


                        // Reviews personale
                        // Duhet te jete para /api/reviews/*
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews/me"
                        ).authenticated()

                        // PUBLIC - reviews sipas eventit
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews/event/*"
                        ).permitAll()

                        // PUBLIC - review sipas ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/reviews/*"
                        ).permitAll()

                        // AUTHENTICATED - krijon review
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/reviews"
                        ).authenticated()

                        // OWNER / ADMIN
                        // Ownership kontrollohet ne Service
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/reviews/*"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/reviews/*"
                        ).authenticated()

                        // WAITLIST

                        // ORGANIZER owner / ADMIN
                        // Duhet te jene para GET /api/waitlists/*
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/waitlists/event/*",
                                "/api/waitlists/event/*/status/*"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER owner / ADMIN
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/waitlists/*/notify",
                                "/api/waitlists/*/convert"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // USER / ORGANIZER / ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/waitlists/event/*"
                        ).authenticated()

                        // Waitlist personale
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/waitlists/my"
                        ).authenticated()

                        // USER owner
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/waitlists/*/cancel"
                        ).authenticated()

                        // USER owner / ORGANIZER owner / ADMIN
                        // Ownership kontrollohet Service
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/waitlists/*"
                        ).authenticated()

                        // NOTIFICATIONS

                        // USER / ORGANIZER / ADMIN
                        // Ownership kontrollohet  Service
                        .requestMatchers(
                                "/api/notifications/**"
                        ).authenticated()

                        // CDO REQUEST TJETER
                        // GJITHMONE I FUNDIT

                        .anyRequest().authenticated()
                )

                // JWT filter ekzekutohet para
                // UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}