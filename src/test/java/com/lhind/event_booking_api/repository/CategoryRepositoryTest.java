package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void setUp() {

        category = Category.builder()
                .nameCategory("Music")
                .descriptionCategory("Music events")
                .build();

        category =
                categoryRepository.save(category);
    }

    @Test
    void findByNameCategory_shouldReturnCategory() {

        Optional<Category> result =
                categoryRepository.findByNameCategory(
                        "Music"
                );

        assertTrue(result.isPresent());

        assertEquals(
                "Music",
                result.get().getNameCategory()
        );

        assertEquals(
                "Music events",
                result.get().getDescriptionCategory()
        );
    }

    @Test
    void findByNameCategory_shouldReturnEmptyWhenCategoryDoesNotExist() {

        Optional<Category> result =
                categoryRepository.findByNameCategory(
                        "Sport"
                );

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByNameCategory_shouldReturnTrue() {

        boolean result =
                categoryRepository.existsByNameCategory(
                        "Music"
                );

        assertTrue(result);
    }

    @Test
    void existsByNameCategory_shouldReturnFalse() {

        boolean result =
                categoryRepository.existsByNameCategory(
                        "Sport"
                );

        assertFalse(result);
    }
}