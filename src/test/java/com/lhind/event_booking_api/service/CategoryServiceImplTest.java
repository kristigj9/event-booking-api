package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.category.CategoryRequest;
import com.lhind.event_booking_api.dto.category.CategoryResponse;
import com.lhind.event_booking_api.entity.Category;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.CategoryMapper;
import com.lhind.event_booking_api.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest request;
    private CategoryResponse response;

    @BeforeEach
    void setUp() {

        request = new CategoryRequest();
        request.setNameCategory("Music");
        request.setDescriptionCategory("Music events");

        category = Category.builder()
                .id(1L)
                .nameCategory("Music")
                .descriptionCategory("Music events")
                .build();

        response = CategoryResponse.builder()
                .id(1L)
                .nameCategory("Music")
                .descriptionCategory("Music events")
                .build();
    }

    @Test
    void createCategory_shouldCreateCategorySuccessfully() {

        when(categoryRepository.existsByNameCategory("Music"))
                .thenReturn(false);

        when(categoryMapper.toEntity(request))
                .thenReturn(category);

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        CategoryResponse result =
                categoryService.createCategory(request);

        assertNotNull(result);
        assertEquals(response, result);

        verify(categoryRepository, times(1))
                .save(category);
    }

    @Test
    void createCategory_shouldThrowExceptionWhenCategoryAlreadyExists() {

        when(categoryRepository.existsByNameCategory("Music"))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> categoryService.createCategory(request)
                );

        assertEquals(
                "Category already exists with name: Music",
                exception.getMessage()
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void getCategoryById_shouldReturnCategorySuccessfully() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        CategoryResponse result =
                categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(response, result);
    }

    @Test
    void getCategoryById_shouldThrowExceptionWhenCategoryNotFound() {

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> categoryService.getCategoryById(99L)
                );

        assertEquals(
                "Category not found with id: 99",
                exception.getMessage()
        );
    }

    @Test
    void getAllCategories_shouldReturnAllCategories() {

        List<Category> categories =
                List.of(category);

        List<CategoryResponse> responses =
                List.of(response);

        when(categoryRepository.findAll())
                .thenReturn(categories);

        when(categoryMapper.toResponseList(categories))
                .thenReturn(responses);

        List<CategoryResponse> result =
                categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void updateCategory_shouldUpdateCategorySuccessfully() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(category))
                .thenReturn(category);

        when(categoryMapper.toResponse(category))
                .thenReturn(response);

        CategoryResponse result =
                categoryService.updateCategory(
                        1L,
                        request
                );

        assertNotNull(result);

        verify(categoryMapper, times(1))
                .updateEntity(
                        request,
                        category
                );

        verify(categoryRepository, times(1))
                .save(category);
    }

    @Test
    void updateCategory_shouldThrowExceptionWhenCategoryNotFound() {

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> categoryService.updateCategory(
                                99L,
                                request
                        )
                );

        assertEquals(
                "Category not found with id: 99",
                exception.getMessage()
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void updateCategory_shouldThrowExceptionWhenNewNameAlreadyExists() {

        CategoryRequest updateRequest =
                new CategoryRequest();

        updateRequest.setNameCategory("Sport");
        updateRequest.setDescriptionCategory("Sport events");

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.existsByNameCategory("Sport"))
                .thenReturn(true);

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> categoryService.updateCategory(
                                1L,
                                updateRequest
                        )
                );

        assertEquals(
                "Category already exists with name: Sport",
                exception.getMessage()
        );

        verify(categoryRepository, never())
                .save(any(Category.class));
    }

    @Test
    void deleteCategory_shouldDeleteCategorySuccessfully() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1))
                .delete(category);
    }

    @Test
    void deleteCategory_shouldThrowExceptionWhenCategoryNotFound() {

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> categoryService.deleteCategory(99L)
                );

        assertEquals(
                "Category not found with id: 99",
                exception.getMessage()
        );

        verify(categoryRepository, never())
                .delete(any(Category.class));
    }
}