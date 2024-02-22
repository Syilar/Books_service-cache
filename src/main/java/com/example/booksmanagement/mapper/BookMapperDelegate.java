package com.example.booksmanagement.mapper;

import com.example.booksmanagement.dto.BookResponse;
import com.example.booksmanagement.dto.UpsertBookRequest;
import com.example.booksmanagement.model.Book;
import com.example.booksmanagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BookMapperDelegate implements BookMapper {

    @Autowired
    private CategoryService categoryService;

    @Override
    public Book requestToBook(UpsertBookRequest request) {
        Book book = new Book();
        book.setName(request.getName());
        book.setAuthor(request.getAuthor());
        book.setCountPage(request.getCountPage());
        book.setCategory(categoryService.findOrCreateCategoryByName(request.getCategoryName()));
        return book;
    }

    @Override
    public Book requestToBook(Long bookId, UpsertBookRequest request) {
        Book book = requestToBook(request);
        book.setId(bookId);
        return book;
    }

    @Override
    public BookResponse bookToResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setName(book.getName());
        bookResponse.setAuthor(book.getAuthor());
        bookResponse.setCountPage(book.getCountPage());
        bookResponse.setCategoryName(book.getCategory().getName());
        return bookResponse;
    }
}
