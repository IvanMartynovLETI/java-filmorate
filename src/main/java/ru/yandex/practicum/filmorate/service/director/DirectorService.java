package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director addDirector(Director director) {
        log.info("Service layer: add director with name: '{}'.", director.getName());

        return directorStorage.addDirector(director);
    }

    public Director getDirectorById(long id) {
        log.info("Service layer: get director by id: '{}'.", id);

        return directorStorage.getDirectorById(id);
    }

    public Director modifyDirector(Director director) {
        log.info("Service layer: modify director with id: '{}'.", director.getId());

        return directorStorage.modifyDirector(director);
    }

    public Director deleteDirector(long id) {
        log.info("Service layer: delete director with id: '{}'.", id);

        return directorStorage.deleteDirector(id);
    }

    public Set<Director> findAllDirectors() {
        log.info("Service layer: get all directors.");

        return directorStorage.findAllDirectors();
    }
}