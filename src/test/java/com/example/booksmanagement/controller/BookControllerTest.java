package com.example.booksmanagement.controller;

import com.example.booksmanagement.AbstractTest;
import com.example.booksmanagement.dto.BookResponse;
import com.example.booksmanagement.dto.UpsertBookRequest;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BookControllerTest extends AbstractTest {

    @Test
    public void whenGetAllBooks_thenReturnBooksList() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());

        String actualResponse = mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(
                bookMapper.bookListToBookListResponse(bookService.findAll()));

        assertFalse(redisTemplate.keys("*").isEmpty());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenGetBooksByCategoryName_thenReturnBooksList() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());

        String actualResponse = mockMvc.perform(get("/api/v1/book/testCategoryName_1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(
                bookMapper.bookListToBookListResponse(bookService.findBooksByNameCategory("testCategoryName_1")));

        assertFalse(redisTemplate.keys("*").isEmpty());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenGetBookByNameAndAuthor_thenReturnOneBook() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());

        String actualResponse = mockMvc.perform(get(
                "/api/v1/book/findByNameAndAuthor" + "?name=" + BOOK_NAME + "&author=" + AUTHOR_NAME))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String expectedResponse = objectMapper.writeValueAsString(
                bookMapper.bookToResponse(bookService.findBookByNameAndAuthor(BOOK_NAME, AUTHOR_NAME)));

        assertFalse(redisTemplate.keys("*").isEmpty());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenCreateBook_thenCreateBookAndEvictCache() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());
        assertEquals(3, databaseBookRepository.count());

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertFalse(redisTemplate.keys("*").isEmpty());

        UpsertBookRequest request = new UpsertBookRequest();
        request.setName("newBook");
        request.setAuthor("newAuthor");
        request.setCountPage(100);
        request.setCategoryName(CATEGORY_NAME);

        String actualResponse = mockMvc.perform(post("/api/v1/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(
                new BookResponse(15L, "newBook", "newAuthor", 100, CATEGORY_NAME));

        assertTrue(redisTemplate.keys("*").isEmpty());
        assertEquals(4, databaseBookRepository.count());

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse, JsonAssert.whenIgnoringPaths("id"));
    }

    @Test
    public void whenUpdateBook_thenUpdateBookAndEvictCache() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertFalse(redisTemplate.keys("*").isEmpty());

        UpsertBookRequest request = new UpsertBookRequest();
        request.setName("updateName");
        request.setAuthor("updateAuthor");
        request.setCountPage(100);
        request.setCategoryName(CATEGORY_NAME);

        String actualResponse = mockMvc.perform(put("/api/v1/book/{id}", UPDATED_ID.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(
                new BookResponse(1L, "updateName", "updateAuthor", 100, CATEGORY_NAME));

        assertTrue(redisTemplate.keys("*").isEmpty());

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse, JsonAssert.whenIgnoringPaths("id"));
    }

    @Test
    public void thenDeleteBookById_thenDeleteBookByIdAndEvictCache() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());
        assertEquals(3, databaseBookRepository.count());

        mockMvc.perform(get("/api/v1/book"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertFalse(redisTemplate.keys("*").isEmpty());

        mockMvc.perform(delete("/api/v1/book/delete/" + UPDATED_ID))
                .andExpect(status().isNoContent());

        assertTrue(redisTemplate.keys("*").isEmpty());
        assertEquals(2, databaseBookRepository.count());
    }
}