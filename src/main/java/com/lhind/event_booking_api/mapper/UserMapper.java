package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.auth.RegisterRequest;
import com.lhind.event_booking_api.dto.reference.UserResponseShort;
import com.lhind.event_booking_api.dto.user.UserResponse;
import com.lhind.event_booking_api.dto.user.UserUpdateRequest;
import com.lhind.event_booking_api.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserMapper {

    //RegisterRequest -> User
    public User toEntity(RegisterRequest request) {

        if (request == null) {
            return null;
        }

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    // User->UserResponse
    public UserResponse toResponse(User user) {

        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    //User-> UserResponseShort

    public UserResponseShort toShortResponse(User user) {

        if (user == null) {
            return null;
        }

        return UserResponseShort.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    //List<User> -> List<UserResponse>

    public List<UserResponse> toResponseList(List<User> users) {

        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(this::toResponse)
                .toList();
    }

    //Update UserUpdateRequest, User
    public void updateEntity(
            UserUpdateRequest request,
            User user
    ) {

        if (request == null || user == null) {
            return;
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
    }
}