package ru.yandex.practicum.filmorate.validator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

@Component
public class Validator {
    private Long peopleId = 0L;
    private Long movieId = 0L;

    public Long getActualFilmId() {
        return ++movieId;
    }

    public Long getActualUserId() {
        return ++peopleId;
    }


    public User validateUser(User user, Map<Long, User> users, boolean isCreationMethod) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (isCreationMethod) {
            user.setId(getActualUserId());
        } else {
            if (!users.containsKey(user.getId())) {
                String userWarning = "User with id: " + user.getId() + " doesn't exist.";
                throw new EntityNotFoundException(userWarning);
            }
        }

        return user;
    }

    public User validateUserInDataBase(User user, JdbcTemplate jdbcTemplate, boolean isCreationMethod) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (isCreationMethod) {
            user.setId(getActualUserId());
        } else {
            String sqlQuery = "SELECT name FROM users WHERE users_id = ?";
            SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, user.getId());
            if (!userRow.next()) {
                String userWarning = "User with id: " + user.getId() + " doesn't exist.";
                throw new EntityNotFoundException(userWarning);
            }
        }

        return user;
    }

    public Film validateFilm(Film film, Map<Long, Film> films, boolean isCreationMethod) {
        if (isCreationMethod) {
            film.setId(getActualFilmId());
        } else {
            if (!films.containsKey(film.getId())) {
                String filmWarning = "Film with id: " + film.getId() + " doesn't exist.";
                throw new EntityNotFoundException(filmWarning);
            }
        }
        return film;
    }

    public Film validateFilmInDataBase(Film film, JdbcTemplate jdbcTemplate, boolean isCreationMethod) {
        if (isCreationMethod) {
            film.setId(getActualFilmId());
        } else {
            String sqlQuery = "SELECT film_name FROM film WHERE film_id = ?";
            SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, film.getId());
            if (!userRow.next()) {
                String filmWarning = "Film with id: " + film.getId() + " doesn't exist.";
                throw new EntityNotFoundException(filmWarning);
            }
        }

        return film;
    }
}