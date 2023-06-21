package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director getDirectorById(long id) {
        return directorStorage.getDirectorById(id);
    }

    public Director modifyDirector(Director director) {
        return directorStorage.modifyDirector(director);
    }

    public Director deleteDirector(long id) {
        return directorStorage.deleteDirector(id);
    }

    public Collection<Director> findAllDirectors() {
        return directorStorage.findAllDirectors();
    }
}