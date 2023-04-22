package ru.yandex.practicum.filmorate.validator;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public class Validator {
    private int peopleId = 0;
    private final String PEOPLE_WARNING = "User doesn't exist.";
    private int movieId = 0;

    private final String MOVIE_WARNING = "Film doesn't exist.";

    public User validatePeople(User user, Map<Integer, User> users, boolean isCreationMethod) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (isCreationMethod) {
            user.setId(++peopleId);
        } else {
            if (!users.containsKey(user.getId())) {
                throw new ValidationException(PEOPLE_WARNING);
            }
        }

        return user;
    }

    public Film validateMovie(Film film, Map<Integer, Film> films, boolean isCreationMethod) {
        if (isCreationMethod) {
            film.setId(++movieId);
        } else {
            if (!films.containsKey(film.getId())) {
                throw new ValidationException(MOVIE_WARNING);
            }
        }

        return film;
    }

    public HttpStatus getStatus(String msg) {
        if (msg.equals(PEOPLE_WARNING) || msg.equals(MOVIE_WARNING)) {
            return HttpStatus.NOT_FOUND;
        } else {
            return HttpStatus.OK;
        }
    }
}