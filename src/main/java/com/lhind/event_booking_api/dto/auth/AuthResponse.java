package com.lhind.event_booking_api.dto.auth;

import com.lhind.event_booking_api.dto.user.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {

    private String token;

    private UserResponse user;
}