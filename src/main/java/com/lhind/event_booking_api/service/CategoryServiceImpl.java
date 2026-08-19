package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.category.CategoryRequest;
import com.lhind.event_booking_api.dto.category.CategoryResponse;
import com.lhind.event_booking_api.entity.Category;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.CategoryMapper;
import com.lhind.event_booking_api.repository.CategoryRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log =
            LogManager.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    // CREATE
    @Override
    @Transactional
    public CategoryResponse createCategory(
            CategoryRequest request
    ) {

        log.info(
                "Creating category with name: {}",
                request.getNameCategory()
        );

        if (categoryRepository.existsByNameCategory(
                request.getNameCategory()
        )) {

            log.warn(
                    "Category creation rejected because category already exists with name: {}",
                    request.getNameCategory()
            );

            throw new DuplicateResourceException(
                    "Category already exists with name: "
                            + request.getNameCategory()
            );
        }

        Category category =
                categoryMapper.toEntity(request);

        Category savedCategory =
                categoryRepository.save(category);

        log.info(
                "Category created successfully with id: {} and name: {}",
                savedCategory.getId(),
                savedCategory.getNameCategory()
        );

        return categoryMapper.toResponse(savedCategory);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(
            Long id
    ) {

        log.debug(
                "Fetching category by id: {}",
                id
        );

        Category category =
                findCategory(id);

        return categoryMapper.toResponse(category);
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        log.debug(
                "Fetching all categories"
        );

        List<Category> categories =
                categoryRepository.findAll();

        return categoryMapper.toResponseList(categories);
    }

    // UPDATE
    @Override
    @Transactional
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request
    ) {

        log.info(
                "Update requested for category id: {}",
                id
        );

        Category category =
                findCategory(id);

        // Kontrollojme duplicate name vetem nese emri ndryshon
        if (!category.getNameCategory()
                .equals(request.getNameCategory())
                && categoryRepository.existsByNameCategory(
                request.getNameCategory()
        )) {

            log.warn(
                    "Category update rejected. Category name already exists: {}",
                    request.getNameCategory()
            );

            throw new DuplicateResourceException(
                    "Category already exists with name: "
                            + request.getNameCategory()
            );
        }

        categoryMapper.updateEntity(
                request,
                category
        );

        Category updatedCategory =
                categoryRepository.save(category);

        log.info(
                "Category updated successfully with id: {}",
                updatedCategory.getId()
        );

        return categoryMapper.toResponse(updatedCategory);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteCategory(
            Long id
    ) {

        log.info(
                "Delete requested for category id: {}",
                id
        );

        Category category =
                findCategory(id);

        categoryRepository.delete(category);

        log.info(
                "Category deleted successfully with id: {}",
                id
        );
    }

    // -----------------------------
    // PRIVATE HELPER METHOD
    // -----------------------------

    private Category findCategory(
            Long id
    ) {

        return categoryRepository.findById(id)
                .orElseThrow(() -> {

                    log.warn(
                            "Category not found with id: {}",
                            id
                    );

                    return new ResourceNotFoundException(
                            "Category not found with id: " + id
                    );
                });
    }
}