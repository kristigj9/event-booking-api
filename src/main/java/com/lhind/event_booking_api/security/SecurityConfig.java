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

                // JWT API -> nuk përdorim CSRF token
                .csrf(csrf -> csrf.disable())

                // JWT -> nuk përdorim session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =====================================
                        // PUBLIC - AUTHENTICATION
                        // =====================================

                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()


                        // =====================================
                        // PUBLIC - EVENTS
                        // =====================================

                        // Çdokush mund të shohë eventet
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/events/**"
                        ).permitAll()


                        // =====================================
                        // CURRENT USER
                        // USER / ORGANIZER / ADMIN
                        // =====================================

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

                        // Ndryshimi i password-it personal
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/users/me/password"
                        ).authenticated()


                        // =====================================
                        // ADMIN - USER MANAGEMENT
                        // =====================================

                        // Merr të gjithë users
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users"
                        ).hasRole("ADMIN")

                        // Merr një user sipas ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/users/*"
                        ).hasRole("ADMIN")

                        // Fshin user
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/users/*"
                        ).hasRole("ADMIN")


                        // =====================================
                        // EVENT MANAGEMENT
                        // ORGANIZER / ADMIN
                        // =====================================

                        // Krijon event
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/events"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // Update event
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/events/*"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // Fshin event
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/events/*"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )


                        // =====================================
                        // VENUES
                        // =====================================

                        // PUBLIC - vetëm lexim
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/venues/**"
                        ).permitAll()

                        // ADMIN - menaxhim venue
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


                        // =====================================
                        // CATEGORIES
                        // =====================================

                        // PUBLIC
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


                        // =====================================
                        // EVENT SEATS
                        // =====================================

                        // PUBLIC - shikon seat-et e eventit
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/event-seats/**"
                        ).permitAll()

                        // ORGANIZER / ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/event-seats/**"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
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


                        // =====================================
                        // BOOKINGS
                        // =====================================

                        // USER / ORGANIZER / ADMIN
                        // Krijon booking
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/bookings"
                        ).authenticated()

                        // Booking-et personale
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/me"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/me/status/*"
                        ).authenticated()

                        // ORGANIZER / ADMIN
                        // Shikon booking-et e një eventi
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/event/*"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // ORGANIZER / ADMIN
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

                        // ORGANIZER / ADMIN
                        // Përfundon booking
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/bookings/*/complete"
                        ).hasAnyRole(
                                "ORGANIZER",
                                "ADMIN"
                        )

                        // Booking sipas ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/bookings/*"
                        ).authenticated()


                        // =====================================
                        // ÇDO REQUEST TJETËR
                        // =====================================

                        .anyRequest().authenticated()
                )

                // JWT filter përpara username/password filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}