package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        log.info("Request for film with name '{}' creation obtained.", film.getName());
        Film checkedMovie = validator.validateMovie(film, films, true);
        films.put(checkedMovie.getId(), checkedMovie);

        return checkedMovie;
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        log.info("Request for film with id '{}'  putting obtained.", film.getId());
        Film checkedMovie = validator.validateMovie(film, films, false);
        films.put(checkedMovie.getId(), checkedMovie);

        return checkedMovie;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Request for getting film's collection obtained. Now {} films present.", films.size());
        return films.values();
    }
}