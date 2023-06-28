package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    private Long reviewId;

    @NotNull(message = "Content of review shouldn't be an empty.")
    @NotBlank(message = "Content of review shouldn't be blank.")
    private String content;

    @NotNull(message = "Review category shouldn't be an empty")
    private Boolean isPositive;

    @NotNull(message = "Id of reviewer shouldn't be an empty")
    private Long userId;

    @NotNull(message = "Id of film shouldn't be an empty")
    private Long filmId;

    private Long useful = 0L;
}

