package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ValidatorTest {
    private static Map<Long, User> users;
    private static Map<Long, Film> films;
    private static Validator validator;
    private static User user;
    private static Film film;

    @BeforeEach
    public void beforeEach() {
        users = new HashMap<>();
        films = new HashMap<>();
        validator = new Validator();

        user = new User();
        user.setId(1L);
        user.setEmail("practicum@yandex.ru");
        user.setLogin("student1");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1983, 1, 12));

        film = new Film();
        film.setId(1L);
        film.setName("Harley Davidson and the Marlboro man");
        film.setDescription("Very nice movie");
        film.setReleaseDate(LocalDate.of(1991, 11, 21));
        film.setDuration(Duration.ofMinutes(98));
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToUpdateUserWhichDoesNotPresentInMap() {
        user.setId(12L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> validator.validateUser(user, users, false));

        String userWarning = "User with id: 12 doesn't exist.";

        assertEquals(userWarning, exception.getMessage());
    }

    @Test
    public void shouldReturnEntityNotFoundExceptionWhileAttemptingToUpdateFilmWhichDoesNotPresentInMap() {
        film.setId(18L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> validator.validateFilm(film, films, false));

        String filmWarning = "Film with id: 18 doesn't exist.";

        assertEquals(filmWarning, exception.getMessage());
    }
}