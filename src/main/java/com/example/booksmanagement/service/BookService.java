package com.example.booksmanagement.service;

import com.example.booksmanagement.model.Book;

import java.util.List;

public interface BookService {

    List<Book> findAll();

    Book findById(Long id);

    Book findBookByNameAndAuthor(String name, String author);

    List<Book> findBooksByNameCategory(String nameCategory);

    Book createBookWithCategory(Book book);

    Book updateBook(Book book);

    void deleteById(Long id);
}
