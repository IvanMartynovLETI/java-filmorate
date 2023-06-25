package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;
    private final Validator validator;
    private final UserService userService;

    private static final int FILMS_COUNT_BY_DEFAULT = 10;

    @Override
    public Film addFilm(Film film) {
        log.info("Request for film with name '{}' creation obtained.", film.getName());
        Film checkedMovie = validator.validateFilm(film, films, true);
        films.put(checkedMovie.getId(), checkedMovie);

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
    public Film deleteFilm(Film film) {
        log.info("Request for film with id '{}' deletion obtained.", film.getId());
        Film checkedMovie = validator.validateFilm(film, films, false);
        films.remove(checkedMovie.getId());

        return checkedMovie;
    }

    @Override
    public Film getFilmById(Long id) {
        log.info("Request for obtaining film by id: {} obtained.", id);
        if (!films.containsKey(id)) {
            throw new EntityNotFoundException("Film with id: " + id + " not found.");
        }
        return films.get(id);
    }

    @Override
    public Collection<Film> findAllMovies() {
        log.info("Request for getting film's collection obtained. Now {} films present.", films.size());
        return films.values();
    }

    @Override
    public Film addLikeToFilm(Long id, Long userId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);

        log.info("User with id: {} set like to film with id: {}", userId, id);

        film.setLikeToFilm(user);
        modifyFilm(film);

        return film;
    }

    @Override
    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        if (filmId == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }
        Film film = getFilmById(filmId);
        User user = userService.getUserById(userId);

        log.info("User with id: {} deletes like from film with id: {}", user.getId(), film.getId());

        film.deleteLikeFromFilm(user);
        modifyFilm(film);

        return film;
    }

    @Override
    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {

        if (count == null) {
            count = FILMS_COUNT_BY_DEFAULT;
        }
        if (count < 0) {
            throw new IncorrectParameterException("'count' parameter equals to null.");
        }
        return findAllMovies().stream()
                .sorted(this::compare).limit(count)
                .collect(Collectors.toList());
    }


    @Override
    public List<Film> getFilmsWithDirector(Long directorId, String sortBy) {
        return null;
    }

    public List<Film> searchFilmsBy(String query, List<String> by) {
        return null;
    }

    private int compare(Film f0, Film f1) {
        return -(f0.getLikesToFilm().size() - f1.getLikesToFilm().size());
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        if (userId == null || friendId == null || userId <= 0 || friendId <= 0) {
            throw new ValidationException("Mistakes");
        }
        return new ArrayList<>(getTopFilms(10, 0, 0));
    }
}