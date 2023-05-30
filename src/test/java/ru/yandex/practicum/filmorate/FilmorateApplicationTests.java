package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.MpaDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmorateApplicationTests {

    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private static User user1;
    private static User user2;
    private static User user3;
    private static Film film1;
    private static Film film2;

    @BeforeAll
    public static void beforeAll() {
        user1 = new User();
        user1.setId(2L);
        user1.setName("User1");
        user1.setLogin("login1");
        user1.setEmail("user1@yandex.ru");
        user1.setBirthday(LocalDate.of(1983, 1, 11));

        user2 = new User();
        user2.setId(3L);
        user2.setName("User2");
        user2.setLogin("login2");
        user2.setEmail("user2@yandex.ru");
        user2.setBirthday(LocalDate.of(1989, 3, 14));

        user3 = new User();
        user3.setId(4L);
        user3.setName("User3");
        user3.setLogin("login3");
        user3.setEmail("user3@yandex.ru");
        user3.setBirthday(LocalDate.of(1985, 8, 18));


        film1 = new Film();
        film1.setId(2L);
        film1.setName("film1");
        film1.setDescription("description1");
        film1.setReleaseDate(LocalDate.of(2023, 4, 12));
        film1.setDuration(Duration.ofMinutes(60L));
        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");
        film1.setMpa(mpa1);

        film2 = new Film();
        film2.setId(3L);
        film2.setName("film2");
        film2.setDescription("description2");
        film2.setReleaseDate(LocalDate.of(2023, 3, 4));
        film2.setDuration(Duration.ofMinutes(90L));
        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");
        film2.setMpa(mpa2);
    }

    //  UserDbStorage methods tests
    @Test
    public void shouldReturnAllUsers() {
        List<User> users = (List<User>) userDbStorage.findAllUsers();

        assertTrue(users.contains(user1) & users.contains(user2) & users.contains(user3) & users.size() == 3,
                "Incorrect operation of findAllUsers() method.");
    }

    @Test
    public void shouldReturnUserByIdOf2() {
        User user1Returned = userDbStorage.getUserById(user1.getId());

        assertEquals(user1, user1Returned);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhileAttemptingToGetUserWithIdOfMinus1() {
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userDbStorage.getUserById(-1L));

        assertEquals("User with id: -1 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldReturnUpdatedUser() {
        user1.setName("User1Updated");
        user1.setLogin("login12");
        user1.setEmail("user11@yandex.ru");
        user1.setBirthday(LocalDate.of(1981, 11, 1));

        userDbStorage.modifyUser(user1);
        User updatedUser1 = userDbStorage.getUserById(user1.getId());

        assertEquals(user1, updatedUser1, "Incorrect operation of getUserById() method.");

        user1.setName("User1");
        user1.setLogin("login1");
        user1.setEmail("user1@yandex.ru");
        user1.setBirthday(LocalDate.of(1983, 1, 11));
        userDbStorage.modifyUser(user1);
    }

    @Test
    public void shouldThrowUserNotFoundExceptionWhileAttemptingToUpdateNonExistingUser() {
        Long id = user1.getId();
        user1.setId(-1L);

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userDbStorage.modifyUser(user1));

        assertEquals("User with id: -1 doesn't exist.", exception.getMessage());

        user1.setId(id);
    }

    @Test
    public void shouldReturnEmptyCollectionOfUsers() {
        userDbStorage.deleteUser(user2);
        userDbStorage.deleteUser(user1);
        userDbStorage.deleteUser(user3);

        List<User> users = (List<User>) userDbStorage.findAllUsers();

        assertTrue(users.isEmpty(),
                "Incorrect operation of findAllUsers() method.");
    }

    @Test
    public void user1ShouldHaveUser2AsFriend() {
        user1 = userDbStorage.addUserToFriends(user1.getId(), user2.getId());

        assertTrue(user1.getFriendsIds().contains(user2.getId()) & user1.getFriendsIds().size() == 1
                & user2.getFriendsIds().isEmpty(), "Incorrect operation of addUserToFriends() method.");

        user1 = userDbStorage.deleteUserFromFriend(user1.getId(), user2.getId());
    }

    @Test
    public void user1RemovedUser2FromFriends() {
        user1 = userDbStorage.addUserToFriends(user1.getId(), user2.getId());
        user1 = userDbStorage.deleteUserFromFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriendsIds().isEmpty() & user2.getFriendsIds().isEmpty(),
                "Incorrect operation of deleteUserFromFriend() method.");
    }

    @Test
    public void shouldReturnEmptyCollectionOfFriends() {
        List<User> users = (List<User>) userDbStorage.getFriendsOfUser(user1.getId());

        assertTrue(users.isEmpty(), "Incorrect operation of getFriendsOfUser() method.");
    }

    @Test
    public void shouldReturnCommonFriend() {
        userDbStorage.addUserToFriends(user1.getId(), user3.getId());
        userDbStorage.addUserToFriends(user2.getId(), user3.getId());
        Optional<List<User>> commons = userDbStorage.getCommonFriends(user1.getId(), user2.getId());

        if (commons.isPresent()) {
            List<User> commonFriends = commons.get();
            assertTrue(Objects.equals(commonFriends.get(0).getId(), user3.getId()) & commons.get().size() == 1,
                    "Incorrect operation of getCommonFriends() method.");
        } else {
            fail("Incorrect operation of getCommonFriends() method. List of commons is empty.");
        }
    }

    @Test
    public void shouldReturnEmptyCollectionOfCommonFriends() {
        Optional<List<User>> commons = userDbStorage.getCommonFriends(user1.getId(), user2.getId());

        if (commons.isPresent()) {
            List<User> commonFriends = commons.get();
            assertTrue(commonFriends.isEmpty(), "Incorrect operation of getCommonFriends() method.");
        } else {
            fail("Incorrect operation of getCommonFriends() method. List of commons is not an empty.");
        }
    }

    @Test
    public void shouldDeleteUser1FromDataBase() {
        userDbStorage.deleteUser(user1);
        List<User> users = (List<User>) userDbStorage.findAllUsers();

        assertTrue(users.contains(user2) & users.contains(user3) & users.size() == 2,
                "Incorrect operation of deleteUser() method.");
    }

    @Test
    public void shouldAddUser() {
        User user4 = new User();
        user4.setName("User4");
        user4.setLogin("login4");
        user4.setEmail("user4@yandex.ru");
        user4.setBirthday(LocalDate.of(1989, 7, 22));

        userDbStorage.addUser(user4);

        User user4Restored = userDbStorage.getUserById(user4.getId());

        assertEquals(user4, user4Restored, "Incorrect operation of addUser() method.");
    }

    //  FilmDbStorage methods tests

    @Test
    public void shouldReturnAllFilms() {
        List<Film> films = (List<Film>) filmDbStorage.findAllMovies();

        assertTrue(films.contains(film1) & films.contains(film2) & films.size() == 2,
                "Incorrect operation of findAllMovies() method.");
    }

    @Test
    public void shouldReturnFilmByIdOf2() {
        Film film1Returned = filmDbStorage.getFilmById(film1.getId());

        assertEquals(film1, film1Returned, "Incorrect operation of getFilmById() method");
    }

    @Test
    public void shouldThrowFilmNotFoundExceptionWhileAttemptingToGetUserWithIdOfMinus1() {
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmDbStorage.getFilmById(-1L));

        assertEquals("Film with id: -1 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldReturnUpdatedFilm() {
        film1.setName("Film1Updated");
        film1.setDescription("description12");
        film1.setReleaseDate(LocalDate.of(1991, 4, 5));
        film1.setDuration(Duration.ofMinutes(65L));

        filmDbStorage.modifyFilm(film1);
        Film updatedFilm1 = filmDbStorage.getFilmById(film1.getId());

        assertEquals(film1, updatedFilm1, "Incorrect operation of modifyFilm() method.");

        film1.setName("film1");
        film1.setDescription("description1");
        film1.setReleaseDate(LocalDate.of(2023, 4, 12));
        film1.setDuration(Duration.ofMinutes(60L));
        filmDbStorage.modifyFilm(film1);
    }

    @Test
    public void shouldThrowFilmNotFoundExceptionWhileAttemptingToUpdateNonExistingFilm() {
        Long id = film1.getId();
        film1.setId(-1L);

        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class,
                () -> filmDbStorage.modifyFilm(film1));

        assertEquals("Film with id: -1 doesn't exist.", exception.getMessage());

        film1.setId(id);
    }

    @Test
    public void shouldReturnEmptyCollectionOfFilms() {
        filmDbStorage.deleteFilm(film2);
        filmDbStorage.deleteFilm(film1);

        List<Film> films = (List<Film>) filmDbStorage.findAllMovies();

        assertTrue(films.isEmpty(),
                "Incorrect operation of findAllMovies() method.");
    }

    @Test
    public void film1ShouldHaveLikeFromUser1() {
        Film film1Liked = filmDbStorage.addLikeToFilm(film1.getId(), user1.getId(), userStorage);

        assertTrue(film1Liked.getLikesToFilm().size() == 1
                        & film1Liked.getLikesToFilm().contains(user1.getId()),
                "Incorrect operation of addLikeToFilm() method.");

        film1 = filmDbStorage.deleteLikeFromFilm(film1.getId(), user1.getId(), userStorage);
    }

    @Test
    public void user1RemovedHisLikeFromFilm1() {
        Film film1Liked = filmDbStorage.addLikeToFilm(film1.getId(), user1.getId(), userStorage);
        film1 = filmDbStorage.deleteLikeFromFilm(film1Liked.getId(), user1.getId(), userStorage);

        assertTrue(film1.getLikesToFilm().isEmpty(),
                "Incorrect operation of deleteLikeFromFilm() method.");
    }

    @Test
    public void shouldAddFilm() {
        Film film3 = new Film();
        film3.setId(1L);
        film3.setName("film3");
        film3.setDescription("description3");
        film3.setReleaseDate(LocalDate.of(2014, 6, 4));
        film3.setDuration(Duration.ofMinutes(62L));
        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");
        film3.setMpa(mpa3);
        Set<Genre> genres = new HashSet<>();
        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");
        Genre genre2 = new Genre();
        genre2.setId(6);
        genre2.setName("Боевик");
        genres.add(genre1);
        genres.add(genre2);
        film3.setGenres(genres);

        filmDbStorage.addFilm(film3);
        Film film3Restored = filmDbStorage.getFilmById(film3.getId());


        assertEquals(film3.getGenres(), film3Restored.getGenres(), "Incorrect operation of addFilm() method.");
    }

    @Test
    public void shouldReturnTopFilm() {
        filmDbStorage.addLikeToFilm(film1.getId(), user1.getId(), userStorage);

        List<Film> topFilms = filmDbStorage.getTopFilms(1);

        assertTrue(topFilms.size() == 1 & topFilms.get(0).getId().equals(film1.getId()),
                "Incorrect operation of getTopFilms() method.");
    }

    //  GenreDbStorage methods tests

    @Test
    public void shouldReturnGenreById() {
        Genre genre = genreDbStorage.getGenreById(1);

        assertTrue(genre.getId() == 1 & genre.getName().equals("Комедия"),
                "Incorrect operation of getGenreById() method.");
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToGetGenreWithIdOfMinus1() {
        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> genreDbStorage.getGenreById(-1));

        assertEquals("id of genre is equal to or less than zero.", exception.getMessage());
    }

    @Test
    public void shouldThrowGenreNotFoundExceptionWhileAttemptingToGetGenreWithIdOf34() {
        final GenreNotFoundException exception = assertThrows(GenreNotFoundException.class,
                () -> genreDbStorage.getGenreById(34));

        assertEquals("Genre with id: 34 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllGenres() {
        List<Genre> genres = (List<Genre>) genreDbStorage.findAllGenres();

        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");

        Genre genre3 = new Genre();
        genre3.setId(3);
        genre3.setName("Мультфильм");

        Genre genre4 = new Genre();
        genre4.setId(4);
        genre4.setName("Триллер");

        Genre genre5 = new Genre();
        genre5.setId(5);
        genre5.setName("Документальный");

        Genre genre6 = new Genre();
        genre6.setId(6);
        genre6.setName("Боевик");

        assertTrue(genres.contains(genre1) & genres.contains(genre2) & genres.contains(genre3)
                & genres.contains(genre4) & genres.contains(genre5) & genres.contains(genre6)
                & genres.size() == 6, "Incorrect operation of findAllGenres() method.");
    }

    //  MpaDbStorage methods tests

    @Test
    public void shouldReturnMpaById() {
        Mpa mpa = mpaDbStorage.getMpaById(1);

        assertTrue(mpa.getId() == 1 & mpa.getName().equals("G"),
                "Incorrect operation of getMpaById() method.");
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToGetMpaWithIdOfMinus1() {
        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> mpaDbStorage.getMpaById(-1));

        assertEquals("id of MPA is equal to or less than zero.", exception.getMessage());
    }

    @Test
    public void shouldThrowMpaNotFoundExceptionWhileAttemptingToGetGenreWithIdOf34() {
        final MpaNotFoundException exception = assertThrows(MpaNotFoundException.class,
                () -> mpaDbStorage.getMpaById(34));

        assertEquals("MPA with id: 34 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllMPA() {
        List<Mpa> mpas = (List<Mpa>) mpaDbStorage.findAllMpa();

        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        mpa1.setName("G");

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");

        Mpa mpa3 = new Mpa();
        mpa3.setId(3);
        mpa3.setName("PG-13");

        Mpa mpa4 = new Mpa();
        mpa4.setId(4);
        mpa4.setName("R");

        Mpa mpa5 = new Mpa();
        mpa5.setId(5);
        mpa5.setName("NC-17");

        assertTrue(mpas.contains(mpa1) & mpas.contains(mpa2) & mpas.contains(mpa3)
                        & mpas.contains(mpa4) & mpas.contains(mpa5) & mpas.size() == 5,
                "Incorrect operation of findAllMpa() method.");
    }
}