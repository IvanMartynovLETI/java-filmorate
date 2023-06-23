package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

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

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }
}