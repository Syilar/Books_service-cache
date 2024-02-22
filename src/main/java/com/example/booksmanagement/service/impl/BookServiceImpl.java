package com.example.booksmanagement.service.impl;

import com.example.booksmanagement.Exception.EntityNotFoundException;
import com.example.booksmanagement.configuration.properties.AppCacheProperties;
import com.example.booksmanagement.model.Book;
import com.example.booksmanagement.model.Category;
import com.example.booksmanagement.repository.DatabaseBookRepository;
import com.example.booksmanagement.service.BookService;
import com.example.booksmanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "redisCacheManager")
public class BookServiceImpl implements BookService {

    private final DatabaseBookRepository repository;

    private final CategoryService categoryService;

    @Override
    @Cacheable(AppCacheProperties.CachesNames.DATABASE_BOOKS)
    public List<Book> findAll() {
        return repository.findAll();
    }

    @Cacheable(value = AppCacheProperties.CachesNames.DATABASE_BOOKS_BY_ID, key = "#id")
    @Override
    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(MessageFormat
                        .format("Книга с ID {} не найдена!", id)));
    }

    @Cacheable(value = AppCacheProperties.CachesNames.DATABASE_BOOKS_BY_NAME_AND_AUTHOR, key = "#name + #author")
    @Override
    public Book findBookByNameAndAuthor(String name, String author) {
        Book probe = new Book();
        probe.setName(name);
        probe.setAuthor(author);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("id", "countPage", "category");

        Example<Book> example = Example.of(probe, matcher);
        return repository.findOne(example).orElseThrow(() ->
                new EntityNotFoundException("Книга с названием и автором  не найдена!"));
    }

    @Cacheable(value = AppCacheProperties.CachesNames.DATABASE_BOOKS_BY_NAME_CATEGORY)
    @Override
    public List<Book> findBooksByNameCategory(String nameCategory) {
        Category category = categoryService.findByName(nameCategory);
        return repository.findAllByCategory(category);
    }

    @CacheEvict(value = {"databaseBooks", "databaseBooksById", "databaseBooksByNameCategory",
            "databaseBooksByNameAndAuthor"}, allEntries = true)
    @Override
    public Book createBookWithCategory(Book book) {
        Book bookForSave = new Book();
        bookForSave.setName(book.getName());
        bookForSave.setAuthor(book.getAuthor());
        bookForSave.setCountPage(book.getCountPage());

        Category category = new Category();
        category.setId(book.getCategory().getId());
        category.setName(book.getCategory().getName());
        category.addBook(bookForSave);

        bookForSave.setCategory(category);
        repository.save(bookForSave);

        return bookForSave;
    }

    @CacheEvict(value = {"databaseBooks", "databaseBooksById", "databaseBooksByNameCategory",
            "databaseBooksByNameAndAuthor"}, allEntries = true)
    @Override
    public Book updateBook(Book book) {
        Book bookForUpdate = findById(book.getId());
        bookForUpdate.setName(book.getName());
        bookForUpdate.setAuthor(book.getAuthor());
        bookForUpdate.setCountPage(book.getCountPage());
        bookForUpdate.setCategory(book.getCategory());
        return repository.save(bookForUpdate);
    }

    @CacheEvict(value = {"databaseBooks", "databaseBooksById", "databaseBooksByNameCategory",
            "databaseBooksByNameAndAuthor"}, allEntries = true)
    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}