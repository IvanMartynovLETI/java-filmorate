package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.constraints.PositiveDuration;
import ru.yandex.practicum.filmorate.constraints.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class Film {
    private int id;
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

    public void setLikeToFilm(User user) {
        likesToFilm.add(user.getId());
    }

    public void deleteLikeFromFilm(User user) {
        likesToFilm.remove(user.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id && Objects.equals(likesToFilm, film.likesToFilm) && Objects.equals(name, film.name)
                && Objects.equals(description, film.description) && Objects.equals(releaseDate, film.releaseDate)
                && Objects.equals(duration, film.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, likesToFilm, name, description, releaseDate, duration);
    }
}