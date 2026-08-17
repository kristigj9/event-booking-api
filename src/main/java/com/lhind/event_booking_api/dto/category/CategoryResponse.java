package com.lhind.event_booking_api.dto.category;

import com.lhind.event_booking_api.dto.reference.CategoryResponseShort;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CategoryResponse {

    private Long id;

    private String nameCategory;

    private String descriptionCategory;
    private List<CategoryResponseShort> categories;
}