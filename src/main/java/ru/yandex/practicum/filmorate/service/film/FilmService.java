package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreDbStorage genreDbStorage;
    private final FeedStorage feedStorage;

    public Film addFilm(Film film) {
        log.info("Service layer: add film with name: '{}'.", film.getName());

        return filmStorage.addFilm(film);
    }

    public Film modifyFilm(Film film) {
        log.info("Service layer: modify film with id: '{}'.", film.getId());

        return filmStorage.modifyFilm(film);
    }

    public Collection<Film> findAll() {
        log.info("Service layer: get all films.");

        return filmStorage.findAllMovies();
    }

    public Film getFilmById(Long id) {
        log.info("Service layer: get film by id: '{}'.", id);

        return filmStorage.getFilmById(id);
    }

    public Film deleteFilm(Film film) {
        log.info("Service layer: delete film with id: '{}'.", film.getId());

        return filmStorage.deleteFilm(film);
    }

    public Film addLikeToFilm(Long id, Long userId) {
        log.info("Service layer: add like to film with id: '{}' from user with id: '{}'.", id, userId);

        Film film = filmStorage.addLikeToFilm(id, userId);
        feedStorage.addFeedList(userId, id, EventType.LIKE, Operation.ADD);

        return film;
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        log.info("Service layer: delete like from film with id: '{}' from user with id: '{}'.", filmId, userId);

        Film film = filmStorage.deleteLikeFromFilm(filmId, userId);
        feedStorage.addFeedList(userId, filmId, EventType.LIKE, Operation.REMOVE);

        return film;
    }

    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        log.info("Service layer: get top films.");

        List<Film> result = filmStorage.getTopFilms(count, genreId, year);
        genreDbStorage.findGenres(result);

        return result;
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        log.info("Service layer: get common films for user with id: '{}' and friend with id '{}'.", userId, friendId);

        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsWithDirector(Long directorId, String sortBy) {
        log.info("Service layer: get films with director by director's id: '{}'.", directorId);

        return filmStorage.getFilmsWithDirector(directorId, sortBy);
    }

    public List<Film> searchFilmsBy(String query, List<String> by) {
        log.info("Service layer: search film.");

        return filmStorage.searchFilmsBy(query, by);
    }
}