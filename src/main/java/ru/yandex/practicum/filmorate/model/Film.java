package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.handler.constraints.PositiveDuration;
import ru.yandex.practicum.filmorate.handler.constraints.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private Long id;
    private Set<Long> likesToFilm = new HashSet<>();

    @NotNull(message = "Film name shouldn't be an empty.")
    @NotBlank(message = "Film name shouldn't be an empty.")
    private String name;

    @Size(min = 1, max = 200, message = "Maximum length of film description should be less than or equal to " +
            "200 characters.")
    private String description;

    @ReleaseDate(message = "Release date should be after than 28.12.1895")
    private LocalDate releaseDate;

    @PositiveDuration(message = "Duration should be positive.")
    private Duration duration;

    private Mpa mpa;
    private Set<Genre> genres = new TreeSet<>();
    private Set<Director> directors = new TreeSet<>();

    public void setLikeToFilm(User user) {
        likesToFilm.add(user.getId());
    }

    public void deleteLikeFromFilm(User user) {
        likesToFilm.remove(user.getId());
    }
}