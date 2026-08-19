package com.lhind.event_booking_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.event_booking_api.dto.category.CategoryRequest;
import com.lhind.event_booking_api.dto.category.CategoryResponse;
import com.lhind.event_booking_api.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private CategoryRequest categoryRequest;

    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {

        CategoryController categoryController =
                new CategoryController(categoryService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .build();

        objectMapper = new ObjectMapper();

        categoryRequest =
                new CategoryRequest();

        categoryRequest.setNameCategory(
                "Music"
        );

        categoryRequest.setDescriptionCategory(
                "Music events and concerts"
        );

        categoryResponse =
                CategoryResponse.builder()
                        .id(1L)
                        .nameCategory(
                                "Music"
                        )
                        .descriptionCategory(
                                "Music events and concerts"
                        )
                        .build();
    }

    @Test
    void createCategory_shouldReturnCreated()
            throws Exception {

        when(categoryService.createCategory(
                any(CategoryRequest.class)
        )).thenReturn(categoryResponse);

        mockMvc.perform(
                        post("/api/categories")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                categoryRequest
                                        )
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.nameCategory")
                                .value("Music")
                )
                .andExpect(
                        jsonPath("$.descriptionCategory")
                                .value(
                                        "Music events and concerts"
                                )
                );

        verify(categoryService, times(1))
                .createCategory(
                        any(CategoryRequest.class)
                );
    }

    @Test
    void getAllCategories_shouldReturnOk()
            throws Exception {

        when(categoryService.getAllCategories())
                .thenReturn(
                        List.of(categoryResponse)
                );

        mockMvc.perform(
                        get("/api/categories")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].nameCategory")
                                .value("Music")
                );

        verify(categoryService, times(1))
                .getAllCategories();
    }

    @Test
    void getCategoryById_shouldReturnOk()
            throws Exception {

        when(categoryService.getCategoryById(1L))
                .thenReturn(categoryResponse);

        mockMvc.perform(
                        get("/api/categories/1")
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.nameCategory")
                                .value("Music")
                );

        verify(categoryService, times(1))
                .getCategoryById(1L);
    }

    @Test
    void updateCategory_shouldReturnOk()
            throws Exception {

        CategoryRequest updateRequest =
                new CategoryRequest();

        updateRequest.setNameCategory(
                "Sport"
        );

        updateRequest.setDescriptionCategory(
                "Sport events"
        );

        CategoryResponse updatedResponse =
                CategoryResponse.builder()
                        .id(1L)
                        .nameCategory(
                                "Sport"
                        )
                        .descriptionCategory(
                                "Sport events"
                        )
                        .build();

        when(categoryService.updateCategory(
                eq(1L),
                any(CategoryRequest.class)
        )).thenReturn(updatedResponse);

        mockMvc.perform(
                        put("/api/categories/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                updateRequest
                                        )
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.nameCategory")
                                .value("Sport")
                )
                .andExpect(
                        jsonPath("$.descriptionCategory")
                                .value(
                                        "Sport events"
                                )
                );

        verify(categoryService, times(1))
                .updateCategory(
                        eq(1L),
                        any(CategoryRequest.class)
                );
    }

    @Test
    void deleteCategory_shouldReturnNoContent()
            throws Exception {

        mockMvc.perform(
                        delete("/api/categories/1")
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(categoryService, times(1))
                .deleteCategory(1L);
    }

    @Test
    void createCategory_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        CategoryRequest invalidRequest =
                new CategoryRequest();

        mockMvc.perform(
                        post("/api/categories")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(categoryService, never())
                .createCategory(
                        any(CategoryRequest.class)
                );
    }

    @Test
    void updateCategory_shouldReturnBadRequestWhenRequestIsInvalid()
            throws Exception {

        CategoryRequest invalidRequest =
                new CategoryRequest();

        mockMvc.perform(
                        put("/api/categories/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                invalidRequest
                                        )
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(categoryService, never())
                .updateCategory(
                        eq(1L),
                        any(CategoryRequest.class)
                );
    }
}