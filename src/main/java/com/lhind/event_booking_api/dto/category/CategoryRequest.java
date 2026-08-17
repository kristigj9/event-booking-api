package com.lhind.event_booking_api.dto.category;

import com.lhind.event_booking_api.dto.reference.CategoryReferenceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100,
            message = "Category name must be between 2 and 100 characters")
    private String nameCategory;

    @Size(max = 500,
            message = "Category description must not exceed 500 characters")
    private String descriptionCategory;
    private List<CategoryReferenceRequest> categories;
}