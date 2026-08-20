package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.reference.EventResponseShort;
import com.lhind.event_booking_api.dto.reference.UserResponseShort;
import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.Event;
import com.lhind.event_booking_api.entity.User;
import com.lhind.event_booking_api.entity.Waitlist;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WaitlistMapper {

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

        UserResponseShort userResponse = null;

        if (waitlist.getUser() != null) {

            userResponse =
                    UserResponseShort.builder()
                            .id(
                                    waitlist.getUser()
                                            .getId()
                            )
                            .firstName(
                                    waitlist.getUser()
                                            .getFirstName()
                            )
                            .lastName(
                                    waitlist.getUser()
                                            .getLastName()
                            )
                            .email(
                                    waitlist.getUser()
                                            .getEmail()
                            )
                            .build();
        }

        EventResponseShort eventResponse = null;

        if (waitlist.getEvent() != null) {

            eventResponse =
                    EventResponseShort.builder()
                            .id(
                                    waitlist.getEvent()
                                            .getId()
                            )
                            .eventName(
                                    waitlist.getEvent()
                                            .getEventName()
                            )
                            .eventStartDateTime(
                                    waitlist.getEvent()
                                            .getEventStartDateTime()
                            )
                            .eventEndDateTime(
                                    waitlist.getEvent()
                                            .getEventEndDateTime()
                            )
                            .eventStatus(
                                    waitlist.getEvent()
                                            .getEventStatus()
                            )
                            .build();
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
                .user(userResponse)
                .event(eventResponse)
                .build();
    }

    public List<WaitlistResponse> toResponseList(
            List<Waitlist> waitlists
    ) {

        return waitlists.stream()
                .map(this::toResponse)
                .toList();
    }
}