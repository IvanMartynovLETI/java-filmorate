package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Validator;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Validator validator = new Validator();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Request for film with id: {} creation obtained.", film.getId());
        Film checkedMovie = film;
        try {
            checkedMovie = validator.validateMovie(film, films, true);
            films.put(checkedMovie.getId(), checkedMovie);
        } catch (ValidationException e) {
            log.warn(e.getMessage());
        }

        return checkedMovie;
    }

    @PutMapping
    public ResponseEntity<Film> put(@RequestBody Film film) {
        log.info("Request for film with id: {} putting obtained.", film.getId());
        HttpStatus status = HttpStatus.OK;
        Film checkedMovie = film;
        try {
            checkedMovie = validator.validateMovie(film, films, false);

            films.put(checkedMovie.getId(), checkedMovie);
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            status = validator.getStatus(e.getMessage());
        }

        return ResponseEntity.status(status).body(checkedMovie);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Request for getting film's collection obtained.");
        return films.values();
    }
}