package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAll() {
        log.info("Request for receiving of all mpa's obtained.");

        return mpaService.findAllMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable final Integer id) {
        log.info("Request for getting mpa by id obtained.");

        return mpaService.getMpaById(id);
    }
}