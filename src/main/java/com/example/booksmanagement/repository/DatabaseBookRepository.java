package com.example.booksmanagement.repository;

import com.example.booksmanagement.model.Book;
import com.example.booksmanagement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatabaseBookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByCategory(Category category);
}
