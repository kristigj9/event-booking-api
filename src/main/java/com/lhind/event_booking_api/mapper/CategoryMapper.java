package com.lhind.event_booking_api.mapper;

import com.lhind.event_booking_api.dto.category.CategoryRequest;
import com.lhind.event_booking_api.dto.category.CategoryResponse;
import com.lhind.event_booking_api.dto.reference.CategoryResponseShort;
import com.lhind.event_booking_api.entity.Category;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CategoryMapper {

    // CategoryRequest -> Category
    public Category toEntity(CategoryRequest request) {

        if (request == null) {
            return null;
        }

        return Category.builder()
                .nameCategory(request.getNameCategory())
                .descriptionCategory(request.getDescriptionCategory())
                .build();
    }

    // Category -> CategoryResponse
    public CategoryResponse toResponse(Category category) {

        if (category == null) {
            return null;
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .nameCategory(category.getNameCategory())
                .descriptionCategory(category.getDescriptionCategory())
                .build();
    }

    // Category -> CategoryResponseShort
    public CategoryResponseShort toShortResponse(Category category) {

        if (category == null) {
            return null;
        }

        return CategoryResponseShort.builder()
                .id(category.getId())
                .nameCategory(category.getNameCategory())
                .build();
    }

    // List<Category> -> List<CategoryResponse>
    public List<CategoryResponse> toResponseList(List<Category> categories) {

        if (categories == null) {
            return Collections.emptyList();
        }

        return categories.stream()
                .map(this::toResponse)
                .toList();
    }

    // List<Category> -> List<CategoryResponseShort>
    public List<CategoryResponseShort> toShortResponseList(
            List<Category> categories
    ) {

        if (categories == null) {
            return Collections.emptyList();
        }

        return categories.stream()
                .map(this::toShortResponse)
                .toList();
    }

    // CategoryRequest + Category ekzistuese -> update
    public void updateEntity(
            CategoryRequest request,
            Category category
    ) {

        if (request == null || category == null) {
            return;
        }

        category.setNameCategory(request.getNameCategory());
        category.setDescriptionCategory(request.getDescriptionCategory());
    }
}