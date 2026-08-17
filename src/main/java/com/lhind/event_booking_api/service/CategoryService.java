package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.category.CategoryRequest;
import com.lhind.event_booking_api.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse getCategoryById(Long id);

    List<CategoryResponse> getAllCategories();

    CategoryResponse updateCategory(
            Long id,
            CategoryRequest request
    );

    void deleteCategory(Long id);
}