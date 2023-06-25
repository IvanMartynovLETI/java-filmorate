package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping

    public Set<Director> findAllDirectors() {

        log.info("Request for receiving of all directors obtained.");
        return directorService.findAllDirectors();
    }

    @PostMapping
    public Director addDirector(@Valid @RequestBody Director director) {
        log.info("Request for director adding obtained.");
        return directorService.addDirector(director);
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        log.info("Request for getting director by id obtained.");
        return directorService.getDirectorById(id);
    }

    @PutMapping
    public Director modifyDirector(@Valid @RequestBody Director director) {
        log.info("Request for director modification obtained.");
        return directorService.modifyDirector(director);
    }

    @DeleteMapping("/{id}")
    public Director deleteDirector(@PathVariable long id) {
        log.info("Request for deleting director by id obtained.");
        return directorService.deleteDirector(id);
    }
}