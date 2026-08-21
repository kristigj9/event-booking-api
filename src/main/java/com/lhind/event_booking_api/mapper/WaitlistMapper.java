package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.entity.Waitlist;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class WaitlistMapper {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public WaitlistMapper(
            UserMapper userMapper,
            EventMapper eventMapper
    ) {
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }

    public Waitlist toEntity(
            WaitlistRequest request,
            User user,
            Event event
    ) {

        if (request == null) {
            return null;
        }

        return Waitlist.builder()
                .requestedSeats(
                        request.getRequestedSeats()
                )
                .user(user)
                .event(event)
                .build();
    }

    public WaitlistResponse toResponse(
            Waitlist waitlist
    ) {

        if (waitlist == null) {
            return null;
        }

        return WaitlistResponse.builder()
                .id(waitlist.getId())
                .requestedSeats(
                        waitlist.getRequestedSeats()
                )
                .createdAt(
                        waitlist.getCreatedAt()
                )
                .status(
                        waitlist.getStatus()
                )
                .user(
                        userMapper.toShortResponse(
                                waitlist.getUser()
                        )
                )
                .event(
                        eventMapper.toShortResponse(
                                waitlist.getEvent()
                        )
                )
                .build();
    }

    public List<WaitlistResponse> toResponseList(
            List<Waitlist> waitlists
    ) {

        if (waitlists == null) {
            return Collections.emptyList();
        }

        return waitlists.stream()
                .map(this::toResponse)
                .toList();
    }
}