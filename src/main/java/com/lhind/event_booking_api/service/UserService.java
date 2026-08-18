package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.user.ChangePasswordRequest;
import com.lhind.event_booking_api.dto.user.RoleUpdateRequest;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.dto.user.UserUpdateRequest;

import java.util.List;

public interface UserService {

    // USER / ORGANIZER / ADMIN - profili personal
    UserResponse getCurrentUser();

    // ADMIN
    UserResponse getUserById(Long id);

    // ADMIN
    List<UserResponse> getAllUsers();

    // USER / ORGANIZER / ADMIN - update i profilit personal
    UserResponse updateCurrentUser(
            UserUpdateRequest request
    );

    // USER / ORGANIZER / ADMIN - ndryshimi i password-it personal
    void changePassword(
            ChangePasswordRequest request
    );

    // ADMIN
    void deleteUser(Long id);

    //USER ROLE
    UserResponse updateUserRole(
            Long id,
            RoleUpdateRequest request
    );

    
}


