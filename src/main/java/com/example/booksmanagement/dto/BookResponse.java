package com.example.booksmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {

    private Long id;

    private String name;

    private String author;

    private int countPage;

    private String categoryName;
}
