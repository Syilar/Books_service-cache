package com.example.booksmanagement.controller;

import com.example.booksmanagement.dto.BookListResponse;
import com.example.booksmanagement.dto.BookResponse;
import com.example.booksmanagement.dto.UpsertBookRequest;
import com.example.booksmanagement.mapper.BookMapper;
import com.example.booksmanagement.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    private final BookMapper mapper;

    @GetMapping
    public ResponseEntity<BookListResponse> findAll() {
        return ResponseEntity.ok(mapper.bookListToBookListResponse(service.findAll()));
    }

    @GetMapping("/findByNameAndAuthor")
    public ResponseEntity<BookResponse> findByNameAndAuthor(@RequestParam String name, @RequestParam String author) {
        return ResponseEntity.ok(mapper.bookToResponse(service.findBookByNameAndAuthor(name, author)));
    }

    @GetMapping("/{nameCategory}")
    public ResponseEntity<BookListResponse> findByNameCategory(@PathVariable String nameCategory) {
        return ResponseEntity.ok(mapper.bookListToBookListResponse(service.findBooksByNameCategory(nameCategory)));
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody @Valid UpsertBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.bookToResponse(service.createBookWithCategory(mapper.requestToBook(request))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @RequestBody UpsertBookRequest request) {
        return ResponseEntity.ok(mapper.bookToResponse(service.updateBook(mapper.requestToBook(id, request))));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
 }
