package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"/schema.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmorateApplicationTests {

    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
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

        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(2, "Драма");
        Set<Genre> genres1 = new TreeSet<>();
        genres1.add(genre1);

        film1 = new Film();
        film1.setId(2L);
        film1.setName("film1");
        film1.setDescription("description1");
        film1.setReleaseDate(LocalDate.of(2023, 4, 12));
        film1.setDuration(Duration.ofMinutes(60L));
        film1.setMpa(new Mpa(1, "G"));
        film1.setGenres(genres1);

        Set<Genre> genres2 = new TreeSet<>();
        genres2.add(genre2);

        film2 = new Film();
        film2.setId(3L);
        film2.setName("film2");
        film2.setDescription("description2");
        film2.setReleaseDate(LocalDate.of(2023, 3, 4));
        film2.setDuration(Duration.ofMinutes(90L));
        film2.setMpa(new Mpa(2, "PG"));
        film2.setGenres(genres2);
    }

    //  UserDbStorage methods tests
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
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToUpdateNonExistingUser() {
        Long id = user1.getId();
        user1.setId(-1L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userDbStorage.modifyUser(user1));

        assertEquals("User with id: -1 doesn't exist.", exception.getMessage());

        user1.setId(id);
    }

    @Test
    public void shouldDeleteUser1FromDataBase() {
        userDbStorage.deleteUser(user1);
        List<User> users = (List<User>) userDbStorage.findAllUsers();

        assertTrue(users.contains(user2) & users.contains(user3) & users.size() == 2,
                "Incorrect operation of deleteUser() method.");
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToDeleteNonExistingUser() {
        Long id = user1.getId();
        user1.setId(-1L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userDbStorage.deleteUser(user1));

        assertEquals("User with id: -1 doesn't exist.", exception.getMessage());

        user1.setId(id);
    }

    @Test
    public void user1ShouldHaveUser2AsFriend() {
        user1 = userDbStorage.addUserToFriends(user1.getId(), user2.getId());

        assertTrue(user1.getFriendsIds().contains(user2.getId()) & user1.getFriendsIds().size() == 1
                & user2.getFriendsIds().isEmpty(), "Incorrect operation of addUserToFriends() method.");

        user1 = userDbStorage.deleteUserFromFriend(user1.getId(), user2.getId());
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToAddFriendToEmptyUser() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> userDbStorage.addUserToFriends(null, user2.getId()));

        assertEquals("'id' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToAddFriendToUserWithIdOfMinus1() {

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userDbStorage.addUserToFriends(-1L, user2.getId()));

        assertEquals("User with id: -1 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToAddEmptyUserAsFriend() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> userDbStorage.addUserToFriends(user1.getId(), null));

        assertEquals("'friendId' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToAddUserWithIdOfMinus1AsFriend() {

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userDbStorage.addUserToFriends(user1.getId(), -1L));

        assertEquals("User with id: -1 doesn't exist.", exception.getMessage());
    }

    @Test
    public void user1RemovedUser2FromFriends() {
        user1 = userDbStorage.addUserToFriends(user1.getId(), user2.getId());
        user1 = userDbStorage.deleteUserFromFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriendsIds().isEmpty() & user2.getFriendsIds().isEmpty(),
                "Incorrect operation of deleteUserFromFriend() method.");
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToDeleteFriendFromEmptyUser() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> userDbStorage.deleteUserFromFriend(null, user2.getId()));

        assertEquals("'id' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToDeleteEmptyFriendFromUser() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> userDbStorage.deleteUserFromFriend(user1.getId(), null));

        assertEquals("'friendId' parameter equals to null.", exception.getMessage());
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
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToGetCommonFriendsWhileUser1IsNull() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> userDbStorage.getCommonFriends(null, user2.getId()));

        assertEquals("'id' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToGetCommonFriendsWhileUser2IsNull() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> userDbStorage.getCommonFriends(user1.getId(), null));

        assertEquals("'otherId' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldReturnUserByIdOf2() {
        User user1Returned = userDbStorage.getUserById(user1.getId());

        assertEquals(user1, user1Returned);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToGetUserFromDbWithIdOfMinus1() {
        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userDbStorage.getUserById(-1L));

        assertEquals("User with id: -1 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllUsers() {
        List<User> users = (List<User>) userDbStorage.findAllUsers();

        assertTrue(users.contains(user1) & users.contains(user2) & users.contains(user3) & users.size() == 3,
                "Incorrect operation of findAllUsers() method.");
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
    public void shouldReturnEmptyCollectionOfFriends() {
        List<User> users = (List<User>) userDbStorage.getFriendsOfUser(user1.getId());

        assertTrue(users.isEmpty(), "Incorrect operation of getFriendsOfUser() method.");
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToGetFriendsFromEmptyUser() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> userDbStorage.getFriendsOfUser(null));

        assertEquals("'id' parameter equals to null.", exception.getMessage());
    }

    //  FilmDbStorage methods tests

    @Test
    public void shouldAddFilm() {
        Film film3 = new Film();
        film3.setId(1L);
        film3.setName("film3");
        film3.setDescription("description3");
        film3.setReleaseDate(LocalDate.of(2014, 6, 4));
        film3.setDuration(Duration.ofMinutes(62L));
        film3.setMpa(new Mpa(3, "PG-13"));
        Set<Genre> genres = new HashSet<>();
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(6, "Боевик");
        genres.add(genre1);
        genres.add(genre2);
        film3.setGenres(genres);

        filmDbStorage.addFilm(film3);
        Film film3Restored = filmDbStorage.getFilmById(film3.getId());

        assertEquals(film3.getGenres(), film3Restored.getGenres(), "Incorrect operation of addFilm() method.");
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
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToUpdateNonExistingFilm() {
        Long id = film1.getId();
        film1.setId(-1L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmDbStorage.modifyFilm(film1));

        assertEquals("Film with id: -1 doesn't exist.", exception.getMessage());

        film1.setId(id);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToDeleteNonExistingFilm() {
        Long id = film1.getId();
        film1.setId(999L);
        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmDbStorage.deleteFilm(film1));

        assertEquals("Film with id: 999 doesn't exist.", exception.getMessage());

        film1.setId(id);
    }

    @Test
    public void shouldReturnFilmByIdOf2() {
        Film film1Returned = filmDbStorage.getFilmById(film1.getId());

        assertEquals(film1, film1Returned, "Incorrect operation of getFilmById() method");
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToGetFilmWithIdOfMinus1() {
        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmDbStorage.getFilmById(-1L));

        assertEquals("Film with id: -1 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllFilms() {
        Set<Long> likesToFilm = new HashSet<>();
        likesToFilm.add(user1.getId());
        film1.setLikesToFilm(likesToFilm);
        filmDbStorage.addLikeToFilm(film1.getId(), user1.getId());
        List<Film> films = (List<Film>) filmDbStorage.findAllMovies();

        assertTrue(films.contains(film1) & films.contains(film2) & films.size() == 2,
                "Incorrect operation of findAllMovies() method.");

        likesToFilm.clear();
        film1.setLikesToFilm(likesToFilm);
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
        Film film1Liked = filmDbStorage.addLikeToFilm(film1.getId(), user1.getId());

        assertTrue(film1Liked.getLikesToFilm().size() == 1
                        & film1Liked.getLikesToFilm().contains(user1.getId()),
                "Incorrect operation of addLikeToFilm() method.");

        film1 = filmDbStorage.deleteLikeFromFilm(film1.getId(), user1.getId());
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToSetLikeToEmptyFilm() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> filmDbStorage.addLikeToFilm(null, user1.getId()));

        assertEquals("'id' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToSetLikeToFilmFromEmptyUser() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> filmDbStorage.addLikeToFilm(film1.getId(), null));

        assertEquals("'userId' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToSetLikeToNonExistingFilm() {
        Long id = film1.getId();
        film1.setId(999L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmDbStorage.addLikeToFilm(film1.getId(), user1.getId()));

        assertEquals("Film with id: 999 doesn't exist.", exception.getMessage());

        film1.setId(id);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToSetLikeToFilmFromNonExistingUser() {
        Long id = user1.getId();
        user1.setId(999L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmDbStorage.addLikeToFilm(film1.getId(), user1.getId()));

        assertEquals("User with id: 999 doesn't exist.", exception.getMessage());

        user1.setId(id);
    }

    @Test
    public void user1RemovedHisLikeFromFilm1() {
        Film film1Liked = filmDbStorage.addLikeToFilm(film1.getId(), user1.getId());
        film1 = filmDbStorage.deleteLikeFromFilm(film1Liked.getId(), user1.getId());

        assertTrue(film1.getLikesToFilm().isEmpty(),
                "Incorrect operation of deleteLikeFromFilm() method.");
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToRemoveLikeFromEmptyFilm() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> filmDbStorage.deleteLikeFromFilm(null, user1.getId()));

        assertEquals("'id' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToDeleteLikeFromFilmFromEmptyUser() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> filmDbStorage.deleteLikeFromFilm(film1.getId(), null));

        assertEquals("'userId' parameter equals to null.", exception.getMessage());
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToDeleteLikeFromNonExistingFilm() {
        Long id = film1.getId();
        film1.setId(999L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmDbStorage.deleteLikeFromFilm(film1.getId(), user1.getId()));

        assertEquals("Film with id: 999 doesn't exist.", exception.getMessage());

        film1.setId(id);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToDeleteLikeFromFilmFromNonExistingUser() {
        Long id = user1.getId();
        user1.setId(999L);

        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmDbStorage.deleteLikeFromFilm(film1.getId(), user1.getId()));

        assertEquals("User with id: 999 doesn't exist.", exception.getMessage());

        user1.setId(id);
    }

    @Test
    public void shouldReturnTopFilmWithLike() {
        filmDbStorage.addLikeToFilm(film1.getId(), user1.getId());

        List<Film> topFilms = filmDbStorage.getTopFilms(1);

        assertTrue(topFilms.size() == 1 & topFilms.get(0).getId().equals(film1.getId()),
                "Incorrect operation of getTopFilms() method.");
    }

    @Test
    public void shouldReturnTopFilmWithListLengthEqualTo1() {

        List<Film> topFilms = filmDbStorage.getTopFilms(1);

        assertTrue(topFilms.size() == 1 & topFilms.get(0).getId().equals(film1.getId()),
                "Incorrect operation of getTopFilms() method.");
    }

    @Test
    public void shouldReturnTopFilmWithDefaultListLength() {

        List<Film> topFilms = filmDbStorage.getTopFilms(null);

        assertTrue(topFilms.size() == 2 & topFilms.get(0).getId().equals(film1.getId())
                        & topFilms.get(1).getId().equals(film2.getId()),
                "Incorrect operation of getTopFilms() method.");
    }

    @Test
    public void shouldThrowIncorrectParameterExceptionWhileAttemptingToGetTopFilmsWithIncorrectListLength() {

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class,
                () -> filmDbStorage.getTopFilms(-1));

        assertEquals("'count' parameter less than zero.", exception.getMessage());
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
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToGetGenreWithIdOf34() {
        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> genreDbStorage.getGenreById(34));

        assertEquals("Genre with id: 34 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllGenres() {
        List<Genre> genres = (List<Genre>) genreDbStorage.findAllGenres();

        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(2, "Драма");
        Genre genre3 = new Genre(3, "Мультфильм");
        Genre genre4 = new Genre(4, "Триллер");
        Genre genre5 = new Genre(5, "Документальный");
        Genre genre6 = new Genre(6, "Боевик");

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
    public void shouldThrowEntityNotFoundExceptionWhileAttemptingToGetMpaWithIdOf34() {
        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> mpaDbStorage.getMpaById(34));

        assertEquals("MPA with id: 34 doesn't exist.", exception.getMessage());
    }

    @Test
    public void shouldReturnAllMPA() {
        List<Mpa> mpas = (List<Mpa>) mpaDbStorage.findAllMpa();

        Mpa mpa1 = new Mpa(1, "G");
        Mpa mpa2 = new Mpa(2, "PG");
        Mpa mpa3 = new Mpa(3, "PG-13");
        Mpa mpa4 = new Mpa(4, "R");
        Mpa mpa5 = new Mpa(5, "NC-17");

        assertTrue(mpas.contains(mpa1) & mpas.contains(mpa2) & mpas.contains(mpa3)
                        & mpas.contains(mpa4) & mpas.contains(mpa5) & mpas.size() == 5,
                "Incorrect operation of findAllMpa() method.");
    }

    @Test
    public void testRecommendationsFilms() {
        filmDbStorage.addLikeToFilm(film1.getId(), user1.getId());
        filmDbStorage.addLikeToFilm(film1.getId(), user2.getId());
        filmDbStorage.addLikeToFilm(film2.getId(), user2.getId());

        Optional<List<Film>> actualFilm = userDbStorage.getRecommendationsFilms(user1.getId());

        assertEquals(1, actualFilm.get().size());
        assertEquals(film2.getId(), actualFilm.get().get(0).getId());
    }
}