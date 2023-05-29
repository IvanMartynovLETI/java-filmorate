package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.genre.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Request for receiving of all genres obtained.");

        return genreService.findAllGenres();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Genre getGenreById(@PathVariable final Integer id) {
        log.info("Request for getting genre by id obtained.");

        return genreService.getGenreById(id);
    }
}