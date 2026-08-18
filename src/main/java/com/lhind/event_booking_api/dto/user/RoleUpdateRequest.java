package com.lhind.event_booking_api.dto.user;

import com.lhind.event_booking_api.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;
}