package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Request for film adding obtained.");

        return filmService.addFilm(film);
    }

    @PutMapping
    public Film put(@RequestBody Film film) {
        log.info("Request for film modification obtained.");

        return filmService.modifyFilm(film);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Request for receiving of all films obtained.");

        return filmService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Film getFilmById(@PathVariable final Long id) {
        log.info("Request for getting film by id obtained.");

        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseBody
    public Film setLikeToFilm(@PathVariable(required = false) final Long id,
                              @PathVariable(required = false) final Long userId) {

        log.info("Request for setting like to film obtained.");

        return filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseBody
    public Film deleteLikeFromFilm(@PathVariable(required = false) final Long id,
                                   @PathVariable(required = false) final Long userId) {

        log.info("Request for deleting like from film obtained.");

        return filmService.deleteLikeFromFilm(id, userId);
    }

    @GetMapping("/popular")
    @ResponseBody
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") Integer count,
                                  @RequestParam(defaultValue = "0") Integer genreId,
                                  @RequestParam(defaultValue = "0") Integer year) {
        log.info("Request for list of top films getting obtained.");

        return filmService.getTopFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsWithDirector(@PathVariable final Long directorId,
                                           @RequestParam(required = false) final String sortBy) {
        log.info("Request for a sorted list of director's films.");

        return filmService.getFilmsWithDirector(directorId, sortBy);
    }
}