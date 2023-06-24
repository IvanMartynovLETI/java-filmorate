package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreDbStorage genreDbStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film modifyFilm(Film film) {
        return filmStorage.modifyFilm(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAllMovies();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLikeToFilm(Long id, Long userId) {
        return filmStorage.addLikeToFilm(id, userId);
    }

    public Film deleteLikeFromFilm(Long filmId, Long userId) {
        return filmStorage.deleteLikeFromFilm(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        List<Film> result = filmStorage.getTopFilms(count, genreId, year);
        genreDbStorage.findGenres(result);
        return result;
    }

    public List<Film> getFilmsWithDirector(Long directorId, String sortBy) {
        return filmStorage.getFilmsWithDirector(directorId, sortBy);
    }
}