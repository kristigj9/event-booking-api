package com.lhind.event_booking_api.service;

import com.lhind.event_booking_api.dto.category.CategoryRequest;
import com.lhind.event_booking_api.dto.category.CategoryResponse;
import com.lhind.event_booking_api.entity.Category;
import com.lhind.event_booking_api.exception.DuplicateResourceException;
import com.lhind.event_booking_api.exception.ResourceNotFoundException;
import com.lhind.event_booking_api.mapper.CategoryMapper;
import com.lhind.event_booking_api.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

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
    public CategoryResponse createCategory(CategoryRequest request) {

        if (categoryRepository.existsByNameCategory(
                request.getNameCategory())) {

            throw new DuplicateResourceException( //Perodrimi i wxceptional per te mos lejuar 2 here nje kategori
                    "Category already exists with name: "
                            + request.getNameCategory()
            );
        }

        Category category = categoryMapper.toEntity(request);

        Category savedCategory =
                categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    // GET BY ID
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );

        return categoryMapper.toResponse(category);
    }

    // GET ALL
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

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

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id//Kontrollojm nese gjendet
                                // ne databaze Kategoria qe duam te ndyshojme sipas id
                        )
                );

        // Kontrollojme nese kategoria qe duam te ndryshojme ka te njejtin emer me ndonje kategori qe ekziston ne database
        if (!category.getNameCategory()
                .equals(request.getNameCategory())
                && categoryRepository.existsByNameCategory(
                request.getNameCategory())) {

            throw new DuplicateResourceException(
                    "Category already exists with name: "
                            + request.getNameCategory()
            );
        }

        categoryMapper.updateEntity(request, category);

        Category updatedCategory =
                categoryRepository.save(category);

        return categoryMapper.toResponse(updatedCategory);
    }

    // DELETE
    @Override
    @Transactional
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id//Kontrollojm nese kategoria gjendet apo jo ne database
                        )
                );

        categoryRepository.delete(category);
    }
}