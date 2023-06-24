package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

public interface DirectorStorage {

    Director addDirector(Director director);

    Director modifyDirector(Director director);

    Director getDirectorById(Long id);

    Director deleteDirector(long id);

    Set<Director> findAllDirectors();
}