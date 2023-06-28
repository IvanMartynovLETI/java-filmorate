package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre getGenreById(int id) {
        log.info("Service layer: get genre with id: '{}'.", id);

        return genreStorage.getGenreById(id);
    }

    public Collection<Genre> findAllGenres() {
        log.info("Service layer: get all genres.");

        return genreStorage.findAllGenres();
    }
}