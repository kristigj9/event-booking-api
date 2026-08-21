package com.lhind.event_booking_api.controller;

import com.lhind.event_booking_api.dto.category.CategoryRequest;
import com.lhind.event_booking_api.dto.category.CategoryResponse;
import com.lhind.event_booking_api.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(
        name = "Categories",
        description = "Endpoints for retrieving and managing event categories"
)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }

    // ADMIN
    @Operation(
            summary = "Create category",
            description = "Creates a new event category. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Category already exists"
            )
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request
    ) {

        CategoryResponse response =
                categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // PUBLIC
    @Operation(
            summary = "Get all categories",
            description = "Returns all event categories"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Categories retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }

    // PUBLIC
    @Operation(
            summary = "Get category by id",
            description = "Returns a category by its id"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(
                        id
                )
        );
    }

    // ADMIN
    @Operation(
            summary = "Update category",
            description = "Updates an existing category. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Category name already exists"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {

        return ResponseEntity.ok(
                categoryService.updateCategory(
                        id,
                        request
                )
        );
    }

    // ADMIN
    @Operation(
            summary = "Delete category",
            description = "Deletes a category by id. Accessible only by ADMIN"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Category deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Category cannot be deleted because it is assigned to one or more events"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User is not authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {

        categoryService.deleteCategory(
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}