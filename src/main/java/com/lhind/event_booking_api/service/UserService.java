package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.user.ChangePasswordRequest;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.dto.user.UserUpdateRequest;

import java.util.List;

public interface UserService {

    // Merr nje user sipas ID
    UserResponse getUserById(Long id);

    // Merr te gjith  users
    List<UserResponse> getAllUsers();

    // Perditeson profilin e user-it
    UserResponse updateUser(Long id, UserUpdateRequest request);

    // Ndryshon password-in
    void changePassword(Long id, ChangePasswordRequest request
    );
    // Fshin user
    void deleteUser(Long id);
}