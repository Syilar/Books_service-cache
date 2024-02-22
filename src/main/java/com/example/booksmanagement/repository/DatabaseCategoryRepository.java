package com.example.booksmanagement.repository;

import com.example.booksmanagement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DatabaseCategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findFirstByName(String name);
}
