package com.lhind.event_booking_api.repository;

import com.lhind.event_booking_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameCategory(String nameCategory);

    boolean existsByNameCategory(String nameCategory);
}