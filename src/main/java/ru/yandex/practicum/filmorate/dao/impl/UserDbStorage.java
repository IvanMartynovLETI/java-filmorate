package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Validator validator;

    public UserDbStorage(JdbcTemplate jdbcTemplate, Validator validator) {
        this.jdbcTemplate = jdbcTemplate;
        this.validator = validator;
    }

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
        log.info("Request to database for obtaining user by id: {} obtained.", id);
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

        log.info("User with id: {} wants to add user with id: {} as friend.", id, friendId);

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

        log.info("Request for database: user with id: {} wants to delete user with id: {} from friends.", id, friendId);

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

        log.info("Request for database: obtaining common friends for user with id: {} and user with id: {}", id,
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

    @Override
    public Optional<List<Film>> getRecommendationsFilms(Long id) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }

        log.info("Request for database: obtaining list of friends for user with id: {}.", id);

        // Лист фильмов которые нравятся целевому пользователю
        List<Integer> likeFilmsUser = jdbcTemplate.queryForList(
                "SELECT film_id FROM film_like WHERE user_id = ?", Integer.class, id);

        // map пользователей со списком фильмов, которые они оценили
        Map<Integer, List<Integer>> usersLikedMovie = new HashMap<>();

        for (Integer filmId : likeFilmsUser) {
            List<Integer> users = jdbcTemplate.queryForList(
                    "SELECT user_id FROM film_like WHERE film_id = ?", Integer.class, filmId);
            if (!(users.size() == 0)) {
                for (Integer userId : users) {
                    List<Integer> filmsUser = jdbcTemplate.queryForList(
                            "SELECT film_id FROM film_like WHERE user_id = ?", Integer.class, userId);
                    usersLikedMovie.put(userId, filmsUser);
                }
            }
        }

        // Содержит количество общих понравившихся фильмов с целевым пользователем
        Map<Integer, Integer> countCrossFilms = new HashMap<>();
        for (Integer userId : usersLikedMovie.keySet()) {
            for (Integer filmId : usersLikedMovie.get(userId)) {
                if(likeFilmsUser.contains(filmId)){
                    if (countCrossFilms.get(userId) == null) {
                        countCrossFilms.put(userId, 1);
                    } else {
                        countCrossFilms.put(userId, countCrossFilms.get(userId) + 1);
                    }
                }
            }
        }


        return Optional.empty();
    }
}