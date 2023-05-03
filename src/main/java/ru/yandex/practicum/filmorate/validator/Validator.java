package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

@Component
public class Validator {
    private Long peopleId = 0L;
    private int movieId = 0;

    public User validateUser(User user, Map<Long, User> users, boolean isCreationMethod) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (isCreationMethod) {
            user.setId(++peopleId);
        } else {
            if (!users.containsKey(user.getId())) {
                String userWarning = "User with id: " + user.getId() + " doesn't exist.";
                throw new UserNotFoundException(userWarning);
            }
        }

        return user;
    }

    public Film validateFilm(Film film, Map<Integer, Film> films, boolean isCreationMethod) {
        if (isCreationMethod) {
            film.setId(++movieId);
        } else {
            if (!films.containsKey(film.getId())) {
                String filmWarning = "Film with id: " + film.getId() + " doesn't exist.";
                throw new FilmNotFoundException(filmWarning);
            }
        }

        return film;
    }
}