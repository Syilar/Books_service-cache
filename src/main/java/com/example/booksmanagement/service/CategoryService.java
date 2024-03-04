package com.example.booksmanagement.service;

import com.example.booksmanagement.model.Category;

public interface CategoryService {

    Category findByName(String name);

    Category findOrCreateCategoryByName(String name);
}
