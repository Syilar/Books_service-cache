package com.example.booksmanagement.mapper;

import com.example.booksmanagement.dto.BookListResponse;
import com.example.booksmanagement.dto.BookResponse;
import com.example.booksmanagement.dto.UpsertBookRequest;
import com.example.booksmanagement.model.Book;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@DecoratedWith(BookMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

    Book requestToBook(UpsertBookRequest request);

    @Mapping(source = "bookId", target = "id")
    Book requestToBook(Long bookId, UpsertBookRequest request);

    @Mapping(source = "book.category.name", target = "categoryName")
    BookResponse bookToResponse(Book book);

    List<BookResponse> bookListToResponseList(List<Book> books);

    default BookListResponse bookListToBookListResponse(List<Book> books) {
        BookListResponse bookListResponse = new BookListResponse();

        bookListResponse.setBooks(books.stream()
        .map(this::bookToResponse).toList());

        return bookListResponse;
    }
}
