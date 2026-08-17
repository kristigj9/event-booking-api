package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.auth.AuthResponse;
import com.lhind.event_booking_api.dto.auth.LoginRequest;
import com.lhind.event_booking_api.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}