package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.HttpStatus;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ValidatorTest {
    private static Map<Integer, User> users;
    private static Map<Integer, Film> films;
    private static Validator validator;
    private static User user;
    private static Film film;
    private final String PEOPLE_WARNING = "User doesn't exist.";
    private final String MOVIE_WARNING = "Film doesn't exist.";

    @BeforeEach
    public void beforeEach() {
        users = new HashMap<>();
        films = new HashMap<>();
        validator = new Validator();

        user = new User();
        user.setId(1);
        user.setEmail("practicum@yandex.ru");
        user.setLogin("student1");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(1983, 1, 12));

        film = new Film();
        film.setId(1);
        film.setName("Harley Davidson and the Marlboro man");
        film.setDescription("Very nice movie");
        film.setReleaseDate(LocalDate.of(1991, 11, 21));
        film.setDuration(Duration.ofMinutes(98));
    }

    @Test
    public void shouldThrowValidationExceptionWhileAttemptingToUpdateUserWhichDoesNotPresentInMap() {
        user.setId(12);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validatePeople(user, users,false));

        assertEquals(PEOPLE_WARNING, exception.getMessage());
    }

    @Test
    public void shouldReturnValidationExceptionWhileAttemptingToUpdateFilmWhichDoesNotPresentInMap() {
        film.setId(18);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validateMovie(film, films,false));

        assertEquals(MOVIE_WARNING, exception.getMessage());
    }

    @Test
    public void shouldReturnHttpStatusNotFoundWhenAcceptingPEOPLE_WARNING() {
        assertEquals(HttpStatus.NOT_FOUND, validator.getStatus(PEOPLE_WARNING),
                "Incorrect response code in shouldReturnHttpStatusNotFoundWhenAcceptingPEOPLE_WARNING " +
                        "test method");
    }

    @Test
    public void shouldReturnHttpStatusNotFoundWhenAcceptingMOVIE_WARNING() {
        assertEquals(HttpStatus.NOT_FOUND, validator.getStatus(MOVIE_WARNING),
                "Incorrect response code in shouldReturnHttpStatusNotFoundWhenAcceptingMOVIE_WARNING " +
                        "test method");
    }

    @Test
    public void shouldReturnHttpStatusOKWhenAcceptingSomethingElseAnother() {
        assertEquals(HttpStatus.OK, validator.getStatus("Something else another"),
                "Incorrect response code in shouldReturnHttpStatusOKWhenAcceptingSomethingElseAnother " +
                        "test method");
    }
}