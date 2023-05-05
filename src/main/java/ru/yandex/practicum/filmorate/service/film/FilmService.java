package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final int FILMS_COUNT_BY_DEFAULT = 10;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film modifyFilm(Film film) {
        return filmStorage.modifyFilm(film);
    }

    public Collection<Film> findAll() {
        return ((InMemoryFilmStorage) filmStorage).findAllMovies();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLikeToFilm(Integer id, Long userId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }
        Film film = filmStorage.getFilmById(id);
        User user = userStorage.getUserById(userId);

        log.info("User with id: {} set like to film with id: {}", user.getId(), film.getId());

        film.setLikeToFilm(user);
        filmStorage.modifyFilm(film);

        return film;
    }

    public Film deleteLikeFromFilm(Integer filmId, Long userId) {
        if (filmId == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        log.info("User with id: {} deletes like from film with id: {}", user.getId(), film.getId());

        film.deleteLikeFromFilm(user);
        filmStorage.modifyFilm(film);

        return film;
    }

    public List<Film> getTopFilms(Integer count) {

        if (count == null) {
            count = FILMS_COUNT_BY_DEFAULT;
        }
        if (count < 0) {
            throw new IncorrectParameterException("'count' parameter equals to null.");
        }
        return ((InMemoryFilmStorage) filmStorage).getFilms().values().stream()
                .sorted(this::compare).limit(count).collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {

        return -(f0.getLikesToFilm().size() - f1.getLikesToFilm().size());
    }
}