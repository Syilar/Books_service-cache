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
        String expectedResponse = objectMapper.writeValueAsString(bookService.findAll());

        assertFalse(redisTemplate.keys("*").isEmpty());
        JsonAssert.assertJsonEquals(expectedResponse, actualResponse);
    }

    @Test
    public void whenGetBooksByCategoryName_thenReturnOneBook() throws Exception {
        assertTrue(redisTemplate.keys("*").isEmpty());

        String actualResponse = mockMvc.perform(get("/api/v1/book/testCategoryName_1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(bookService.findBooksByNameCategory("testCategoryName_1"));

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
        String actualResponse = mockMvc.perform(post("api/v1/book")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(new BookResponse(5L, "newBook", "newAuthor", 120, CATEGORY_NAME));

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
        String actualResponse = mockMvc.perform(put("api/v1/book/{id}", UPDATED_ID.toString())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String expectedResponse = objectMapper.writeValueAsString(new BookResponse(5L, "updateName", "newAuthor", 120, CATEGORY_NAME));

        assertTrue(redisTemplate.keys("*").isEmpty());
        assertEquals(4, databaseBookRepository.count());

        JsonAssert.assertJsonEquals(expectedResponse, actualResponse, JsonAssert.whenIgnoringPaths("id"));
    }
}
