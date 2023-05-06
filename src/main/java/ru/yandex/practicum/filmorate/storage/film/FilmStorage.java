package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Film deleteFilm(Film film);

    Film modifyFilm(Film film);

    Film getFilmById(int id);

    Collection<Film> findAllMovies();
}