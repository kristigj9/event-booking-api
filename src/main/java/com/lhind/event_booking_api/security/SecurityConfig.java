package com.lhind.event_booking_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // Security Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // JWT API -> nuk perdorim CSRF token
                .csrf(csrf -> csrf.disable())

                // JWT -> nuk perdorim session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
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
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()


                        // EVENTS

                        // PUBLIC - shikon eventet
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/events/**"
                        ).permitAll()

                        // ORGANIZER / ADMIN - krijon event
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/events"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER owner / ADMIN
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/events/*"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER owner / ADMIN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/events/*"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )


                        // CURRENT USER
                        // USER / ORGANIZER / ADMIN

                        // Profili personal
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/me"
                        ).authenticated()

                        // Update i profilit personal
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/users/me"
                        ).authenticated()

                        // Ndryshimi i password-it
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/users/me/password"
                        ).authenticated()


                        // ADMIN - USER MANAGEMENT

                        // Merr te gjithe users
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/users/*/role"
                        ).hasRole("ADMIN")


                        // Merr nje user sipas ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/*"
                        ).hasRole("ADMIN")

                        // Fshin user
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/*"
                        ).hasRole("ADMIN")


                        // VENUES

                        // PUBLIC - vetem lexim
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/venues/**"
                        ).permitAll()

                        // ADMIN - krijon venue
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/venues/**"
                        ).hasRole("ADMIN")

                        // ADMIN - perditeson venue
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/venues/**"
                        ).hasRole("ADMIN")

                        // ADMIN - fshin venue
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

                        // ADMIN - krijon category
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        // ADMIN - perditeson category
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        // ADMIN - fshin category
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

                        // ADMIN - krijon seat
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/seats/**"
                        ).hasRole("ADMIN")

                        // ADMIN - perditeson seat
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/seats/**"
                        ).hasRole("ADMIN")

                        // ADMIN - fshin seat
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/seats/**"
                        ).hasRole("ADMIN")


                        // EVENT SEATS

                        // PUBLIC - shikon seat-et e eventit
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/event-seats/**"
                        ).permitAll()

                        // ORGANIZER owner / ADMIN
                        // Krijon EventSeat
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/event-seats/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER owner / ADMIN
                        // Perditeson cmimin
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/event-seats/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER owner / ADMIN
                        // Fshin EventSeat
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/event-seats/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )


                        // BOOKINGS

                        // USER / ORGANIZER / ADMIN
                        // Krijon booking per user-in aktual
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/bookings"
                        ).authenticated()

                        // Booking-et personale
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/me"
                        ).authenticated()

                        // Booking-et personale sipas statusit
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/me/status/*"
                        ).authenticated()

                        // ORGANIZER owner / ADMIN
                        // Shikon booking-et e nje eventi
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/event/*"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER owner / ADMIN
                        // Konfirmon booking
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/bookings/*/confirm"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // USER owner / ADMIN
                        // Anulon booking
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/bookings/*/cancel"
                        ).authenticated()

                        // ORGANIZER owner / ADMIN
                        // Perfundon booking
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/bookings/*/complete"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // USER owner / ADMIN
                        // Merr booking sipas ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/*"
                        ).authenticated()

                                // PAYMENTS
                                // USER owner / ADMIN
                        // Krijon payment per booking
                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/payments/booking/*"
                                ).authenticated()
                                // USER owner / ADMIN
                                // Merr payment sipas booking
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/payments/booking/*"
                                ).authenticated()
                                // USER owner / ADMIN
                                // Merr payment sipas ID
                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/payments/*"
                                ).authenticated()
                                // USER owner / ADMIN
                                // Perfundon payment
                                .requestMatchers(
                                        HttpMethod.PATCH,
                                        "/api/payments/*/complete"
                                ).authenticated()

                                // USER owner / ADMIN
                                // Refund payment
                                .requestMatchers(
                                        HttpMethod.PATCH,
                                        "/api/payments/*/refund"
                                ).authenticated()

                        // REVIEWS

                        // AUTHENTICATED - reviews personale
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

                        // OWNER / ADMIN
                        // Ownership kontrollohet ne Service
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/reviews/*"
                        ).authenticated()


                        // CDO REQUEST TJETER

                        .anyRequest().authenticated()
                )

                // JWT filter perpara username/password filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}