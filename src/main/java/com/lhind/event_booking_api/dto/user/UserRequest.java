package com.lhind.event_booking_api.dto.user;

import com.lhind.event_booking_api.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Role role;
}