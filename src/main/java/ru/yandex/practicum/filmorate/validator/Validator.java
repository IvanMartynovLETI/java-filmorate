package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public class Validator {
    private int peopleId = 0;
    private int movieId = 0;

    public User validatePeople(User user, Map<Integer, User> users, boolean isCreationMethod) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (isCreationMethod) {
            user.setId(++peopleId);
        } else {
            if (!users.containsKey(user.getId())) {
                String peopleWarning = "User doesn't exist.";
                throw new ValidationException(peopleWarning);
            }
        }

        return user;
    }

    public Film validateMovie(Film film, Map<Integer, Film> films, boolean isCreationMethod) {
        if (isCreationMethod) {
            film.setId(++movieId);
        } else {
            if (!films.containsKey(film.getId())) {
                String movieWarning = "Film doesn't exist.";
                throw new ValidationException(movieWarning);
            }
        }

        return film;
    }
}