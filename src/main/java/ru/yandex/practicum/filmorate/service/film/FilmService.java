package ru.yandex.practicum.filmorate.service.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class FilmService {
    private Validator validator;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLikeToFilm(Film film, User user) {
        log.info("User with id: {} set like to film with id: {}", user.getId(), film.getId());

        film.setLikeToFilm(user);
        filmStorage.modifyFilm(film);

        return film;
    }

    public Film deleteLikeFromFilm(Film film, User user) {
        log.info("User with id: {} deletes like from film with id: {}", user.getId(), film.getId());

        film.deleteLikeFromFilm(user);
        filmStorage.modifyFilm(film);

        return film;
    }

    public List<Film> getTopFilms(int count) {
        return ((InMemoryFilmStorage) filmStorage).getFilms().values().stream()
                .sorted(this::compare).limit(count).collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {

        return -(f0.getLikesToFilm().size() - f1.getLikesToFilm().size());
    }
}