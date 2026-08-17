package com.lhind.event_booking_api.dto.reference;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseShort {

    private Long id;

    private String firstName;

    private String lastName;
}