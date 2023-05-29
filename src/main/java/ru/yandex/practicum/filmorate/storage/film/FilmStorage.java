package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film modifyFilm(Film film);

    Film deleteFilm(Film film);

    Film getFilmById(Long id);

    Collection<Film> findAllMovies();

    Film addLikeToFilm(Long id, Long userId, UserStorage userStorage);

    Film deleteLikeFromFilm(Long filmId, Long userId, UserStorage userStorage);

    List<Film> getTopFilms(Integer count);
}