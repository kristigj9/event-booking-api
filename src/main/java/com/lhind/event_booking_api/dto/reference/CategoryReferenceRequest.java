package com.lhind.event_booking_api.dto.reference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryReferenceRequest {
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100,
            message = "Category name must be between 2 and 100 characters")
    private String nameCategory;
}