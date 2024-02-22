package com.example.booksmanagement.service.impl;

import com.example.booksmanagement.Exception.EntityNotFoundException;
import com.example.booksmanagement.model.Category;
import com.example.booksmanagement.repository.DatabaseCategoryRepository;
import com.example.booksmanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final DatabaseCategoryRepository repository;

    @Override
    public Category findByName(String name) {
        return repository.findFirstByName(name).orElseThrow(() ->
                new EntityNotFoundException("Книги с категорией {} не найдены!"));
    }

    @Override
    public Category findOrCreateCategoryByName(String name) {
        if (repository.findFirstByName(name).isPresent()) {
            return repository.findFirstByName(name).get();
        }

        Category newCategory = new Category();
        newCategory.setName(name);
        return repository.save(newCategory);
    }
}
