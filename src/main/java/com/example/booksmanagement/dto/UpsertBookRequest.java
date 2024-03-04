package com.example.booksmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpsertBookRequest {

    @NotBlank(message = "Название книги должно быть указано!")
    @Size(min = 2, message = "Название книги не может быть меньше {min}!")
    private String name;

    @NotBlank(message = "Автор книги должен быть указан!")
    private String author;

    @Positive(message = "Количество страниц должно быть больше 0!")
    private int countPage;

    @NotBlank(message = "Категория книги должна быть указана!")
    @Size(min = 4, message = "Название категории не может быть меньше {min}!")
    private String categoryName;
}
