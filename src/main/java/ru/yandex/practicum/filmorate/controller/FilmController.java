package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final UserStorage userStorage;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film put(@RequestBody Film film) {

        return filmStorage.modifyFilm(film);
    }

    @GetMapping
    public Collection<Film> findAll() {

        return ((InMemoryFilmStorage) filmStorage).findAllMovies();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Film getFilmById(@PathVariable final Integer id) {

        return filmStorage.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseBody
    public Film setLikeToFilm(@PathVariable(required = false) final Integer id,
                              @PathVariable(required = false) final Long userId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }

        return filmService.addLikeToFilm(filmStorage.getFilmById(id), userStorage.getUserById(userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseBody
    public Film deleteLikeFromFilm(@PathVariable(required = false) final Integer id,
                                   @PathVariable(required = false) final Long userId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }

        return filmService.deleteLikeFromFilm(filmStorage.getFilmById(id), userStorage.getUserById(userId));
    }

    @GetMapping("/popular")
    @ResponseBody
    public List<Film> getTopFilms(@RequestParam(required = false) final Integer count) {
        int countByDefault = 10;
        if (count == null) {
            return filmService.getTopFilms(countByDefault);
        }
        if (count < 0) {
            throw new IncorrectParameterException("'count' parameter equals to null.");
        }
        return filmService.getTopFilms(count);
    }
}