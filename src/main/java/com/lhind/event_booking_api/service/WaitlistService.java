package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.waitlist.WaitlistRequest;
import com.lhind.event_booking_api.dto.waitlist.WaitlistResponse;
import com.lhind.event_booking_api.entity.WaitlistStatus;

import java.util.List;

public interface WaitlistService {

    WaitlistResponse joinWaitlist(
            Long eventId,
            WaitlistRequest request
    );

    WaitlistResponse getWaitlistById(
            Long waitlistId
    );

    List<WaitlistResponse> getMyWaitlists();

    List<WaitlistResponse> getWaitlistByEvent(
            Long eventId
    );

    List<WaitlistResponse> getWaitlistByEventAndStatus(
            Long eventId,
            WaitlistStatus status
    );

    WaitlistResponse markAsNotified(
            Long waitlistId
    );

    WaitlistResponse markAsConverted(
            Long waitlistId
    );

    WaitlistResponse cancelWaitlist(
            Long waitlistId
    );
}