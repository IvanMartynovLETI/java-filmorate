package ru.yandex.practicum.filmorate.storage.film;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
@Data
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private final Validator validator;

    @Override
    public Film addFilm(Film film) {
        log.info("Request for film with name '{}' creation obtained.", film.getName());
        Film checkedMovie = validator.validateFilm(film, films, true);
        films.put(checkedMovie.getId(), checkedMovie);

        return checkedMovie;
    }

    @Override
    public Film deleteFilm(Film film) {
        log.info("Request for film with id '{}' deletion obtained.", film.getId());
        Film checkedMovie = validator.validateFilm(film, films, false);
        films.remove(checkedMovie.getId());

        return checkedMovie;
    }

    @Override
    public Film modifyFilm(Film film) {
        log.info("Request for film with id '{}' update obtained.", film.getId());
        Film checkedMovie = validator.validateFilm(film, films, false);
        films.put(checkedMovie.getId(), checkedMovie);

        return checkedMovie;
    }

    @Override
    public Film getFilmById(int id) {
        log.info("Request for obtaining film by id: {} obtained.", id);
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Film with id: " + id + " not found.");
        }

        return films.get(id);
    }

    public Collection<Film> findAllMovies() {
        log.info("Request for getting film's collection obtained. Now {} films present.", films.size());
        return films.values();
    }
}