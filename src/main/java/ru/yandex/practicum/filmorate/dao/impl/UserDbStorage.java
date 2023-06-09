package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Validator validator;

    @Override
    public User addUser(User user) {
        log.info("Request to database for user with login '{}' creation obtained.", user.getLogin());

        User checkedPeople = validator.validateUserInDataBase(user, jdbcTemplate, true);
        String sqlQuery = "INSERT INTO users(users_id, name, email, login, birthday) VALUES(?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                checkedPeople.getId(),
                checkedPeople.getName(),
                checkedPeople.getEmail(),
                checkedPeople.getLogin(),
                checkedPeople.getBirthday());

        return getUserById(checkedPeople.getId());
    }

    @Override
    public User modifyUser(User user) {
        log.info("Request to database for user with id '{}' update obtained.", user.getId());

        User checkedPeople = validator.validateUserInDataBase(user, jdbcTemplate, false);
        String sqlQuery = "SELECT name FROM users WHERE users_id = ?";
        jdbcTemplate.queryForRowSet(sqlQuery, checkedPeople.getId());

        sqlQuery = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE users_id = ?";
        jdbcTemplate.update(sqlQuery,
                checkedPeople.getName(),
                checkedPeople.getEmail(),
                checkedPeople.getLogin(),
                checkedPeople.getBirthday(),
                checkedPeople.getId());

        jdbcTemplate.update("DELETE FROM user_friends_status WHERE user_id = ?", checkedPeople.getId());
        jdbcTemplate.update("DELETE FROM user_friends_status WHERE friend_id = ?", checkedPeople.getId());

        return getUserById(checkedPeople.getId());
    }

    @Override
    public User deleteUser(User user) {
        log.info("Request to database for user with id '{}' deletion obtained.", user.getId());

        User checkedPeople = validator.validateUserInDataBase(user, jdbcTemplate, false);
        String sqlQuery = "SELECT name FROM users WHERE users_id = ?";
        jdbcTemplate.queryForRowSet(sqlQuery, checkedPeople.getId());
        jdbcTemplate.update("DELETE FROM users WHERE users_id = ?", checkedPeople.getId());

        return checkedPeople;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Request to database for obtaining user by id: '{}' obtained.", id);

        User user = new User();
        String sqlQuery = "SELECT * FROM users WHERE users_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (!userRow.next()) {
            String userWarning = "User with id: " + id + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        } else {
            user.setId(userRow.getLong("users_id"));
            user.setFriendsIds(new HashSet<>(jdbcTemplate.queryForList("SELECT friend_id FROM " +
                            "user_friends_status WHERE user_id = ? AND status_of_friendship = ?", Long.class, id,
                    "Confirmed")));
            user.setName(userRow.getString("name"));
            user.setEmail(userRow.getString("email"));
            user.setLogin(userRow.getString("login"));
            user.setBirthday(Objects.requireNonNull(userRow.getDate("birthday")).toLocalDate());
        }

        return user;
    }

    @Override
    public Collection<User> findAllUsers() {
        log.info("Request to database for all users collecting obtained.");

        String sqlQuery = "SELECT * FROM users";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilledUser(rs.getLong("users_id"),
                new HashSet<>(jdbcTemplate.queryForList("SELECT " + "friend_id FROM user_friends_status" +
                        " WHERE user_id = ?", Long.class, rs.getLong("users_id"))),
                rs.getString("email"), rs.getString("login"),
                rs.getString("name"), rs.getDate("birthday").toLocalDate()));
    }

    @Override
    public User addUserToFriends(Long id, Long friendId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (id <= 0) {
            String userWarning = "User with id: " + id + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        }
        if (friendId == null) {
            throw new IncorrectParameterException("'friendId' parameter equals to null.");
        }
        if (friendId <= 0) {
            String userWarning = "User with id: " + friendId + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        }

        String sqlQuery = "SELECT name FROM users WHERE users_id = ?";
        jdbcTemplate.queryForRowSet(sqlQuery, id);

        log.info("User with id: '{}' wants to add user with id: '{}' as friend.", id, friendId);

        jdbcTemplate.update("INSERT INTO user_friends_status(user_id, friend_id, status_of_friendship)" +
                "VALUES(?, ?, ?)", id, friendId, "Confirmed");
        jdbcTemplate.update("INSERT INTO user_friends_status(user_id, friend_id, status_of_friendship) " +
                "VALUES(?, ?, ?)", friendId, id, "Unconfirmed");

        return getUserById(id);
    }

    @Override
    public User deleteUserFromFriend(Long id, Long friendId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (friendId == null) {
            throw new IncorrectParameterException("'friendId' parameter equals to null.");
        }

        log.info("Request for database: user with id: '{}' wants to delete user with id: '{}' from friends.", id,
                friendId);

        jdbcTemplate.update("DELETE FROM user_friends_status WHERE user_id = ? AND friend_id = ? AND " +
                "status_of_friendship = ?", id, friendId, "Confirmed");
        jdbcTemplate.update("DELETE FROM user_friends_status WHERE user_id = ? AND friend_id = ? AND " +
                "status_of_friendship = ?", friendId, id, "Unconfirmed");

        return getUserById(id);
    }

    @Override
    public Optional<List<User>> getCommonFriends(Long id, Long otherId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (otherId == null) {
            throw new IncorrectParameterException("'otherId' parameter equals to null.");
        }

        User user = getUserById(id);
        User otherUser = getUserById(otherId);

        log.info("Request for database: obtaining common friends for user with id: '{}' and user with id: '{}'.", id,
                otherId);

        List<User> commonFriends;

        Set<Long> friendsIdsForUser1 = new HashSet<>(user.getFriendsIds());
        friendsIdsForUser1.retainAll(otherUser.getFriendsIds());

        Map<Long, User> users = findAllUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        commonFriends = friendsIdsForUser1.stream()
                .filter(users::containsKey)
                .map(users::get)
                .collect(Collectors.toList());

        return Optional.of(commonFriends);
    }

    @Override
    public Collection<User> getFriendsOfUser(Long id) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }

        User user = getUserById(id);

        log.info("Request for database: obtaining list of friends for user with id: {}.", id);

        Map<Long, User> users = findAllUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return user.getFriendsIds().stream()
                .filter(users::containsKey)
                .map(users::get)
                .collect(Collectors.toList());
    }

    private User makeFilledUser(Long id, Set<Long> friendsIds, String email, String login, String name,
                                LocalDate birthday) {
        User user = new User();
        if (id != null) {
            user.setId(id);
        }
        if (!friendsIds.isEmpty()) {
            user.setFriendsIds(friendsIds);
        }
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);

        return user;
    }

    public Optional<List<Film>> getRecommendationsFilms(Long id) {

        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }

        List<Film> finalFilms = new ArrayList<>();

        log.info("Request to database for getting recommendations by user id: '{}'.", id);

        // Получаем film_id рекомендованных фильмов
        List<String> recommendationsFilmsId = jdbcTemplate.queryForList(
                " SELECT DISTINCT film_id " +
                        " FROM film_like " +
                        " JOIN (SELECT dop_user.user_id," +
                        "       COUNT(dop_user.film_id) AS count_films" +
                        "       FROM (SELECT * FROM film_like" +
                        "             WHERE user_id != ?) AS dop_user" +
                        "       JOIN (SELECT * FROM film_like" +
                        "             WHERE user_id = ?) AS base_user" +
                        "       ON base_user.film_id = dop_user.film_id" +
                        "       GROUP BY dop_user.user_id" +
                        "       ORDER BY count_films DESC" +
                        "       LIMIT (SELECT CEILING(COUNT(user_id) * 0.1)" +
                        "              FROM film_like" +
                        "              WHERE film_id IN (SELECT film_id FROM film_like " +
                        "                                WHERE user_id = ?))) AS user_top" +
                        " ON film_like.user_id = user_top.user_id" +
                        " WHERE film_like.film_id not IN (SELECT film_id FROM film_like " +
                        "                                 WHERE user_id = ?)", String.class, id, id, id, id);

        if (recommendationsFilmsId.size() == 0) {
            return Optional.of(finalFilms);
        }

        SqlRowSet filmsRow = jdbcTemplate.queryForRowSet(
                " SELECT f.film_id, " +
                        "       f.film_name, " +
                        "       f.film_description, " +
                        "       f.release_date, " +
                        "       f.duration, " +
                        "       f.mpa_id, " +
                        "       GROUP_CONCAT(fl.user_id) AS listOfUsersLike," +
                        "       m.mpa_name " +
                        " FROM film AS f " +
                        " JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                        " JOIN film_like AS fl ON fl.film_id = f.film_id" +
                        " JOIN users AS u ON fl.user_id = u.users_id" +
                        " WHERE f.film_id IN (?)", String.join(",", recommendationsFilmsId));

        while (filmsRow.next()) {
            Film film = new Film();
            film.setId(filmsRow.getLong("film_id"));
            film.setLikesToFilm(Arrays.stream(Objects.requireNonNull(filmsRow.getString("listOfUsersLike"))
                            .split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toSet()));
            film.setName(filmsRow.getString("film_name"));
            film.setDescription(filmsRow.getString("film_description"));
            film.setReleaseDate(Objects.requireNonNull(filmsRow.getDate("release_date")).toLocalDate());
            film.setDuration(Duration.ofMinutes(filmsRow.getInt("duration")));
            film.setMpa(new Mpa(filmsRow.getInt("mpa_id"), filmsRow.getString("mpa_name")));

            finalFilms.add(film);
        }

        SqlRowSet genresRow = jdbcTemplate.queryForRowSet(
                " SELECT genre.genre_id, " +
                        "        genre_name," +
                        "        f.film_id" +
                        " FROM genre " +
                        " JOIN film_genre ON genre.genre_id = film_genre.genre_id " +
                        " JOIN film AS f ON film_genre.film_id = f.film_id" +
                        " WHERE f.film_id IN (?)", String.join(",", recommendationsFilmsId));
        HashMap<Long, Set<Genre>> genres = new HashMap<>();
        while (genresRow.next()) {
            Genre genre = new Genre(genresRow.getInt("genre_id"), genresRow
                    .getString("genre_name"));
            genres.computeIfAbsent(genresRow.getLong("film_id"), k -> new TreeSet<>()).add(genre);
        }
        for (Film film : finalFilms) {
            film.setGenres(genres.get(film.getId()));
        }
        return Optional.of(finalFilms);
    }
}