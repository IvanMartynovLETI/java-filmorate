package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film modifyFilm(Film film);

    Film deleteFilm(Film film);

    Film getFilmById(Long id);

    Collection<Film> findAllMovies();

    Film addLikeToFilm(Long id, Long userId);

    Film deleteLikeFromFilm(Long filmId, Long userId);

    List<Film> getTopFilms(Integer count);

    List<Film> getFilmsWithDirector(Long directorId, String sortBy);

    List<Film> searchFilmsBy(String query, List<String> by);
}