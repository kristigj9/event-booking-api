package com.lhind.event_booking_api.dto.category;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CategoryResponse {

    private Long id;

    private String nameCategory;

    private String descriptionCategory;
}