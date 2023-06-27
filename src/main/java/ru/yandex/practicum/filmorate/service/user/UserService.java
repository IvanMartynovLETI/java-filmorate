package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public User addUser(User user) {
        log.info("Service layer: get user with login: '{}'.", user.getLogin());

        return userStorage.addUser(user);
    }

    public User modifyUser(User user) {
        log.info("Service layer: modify user with id: '{}'.", user.getId());

        return userStorage.modifyUser(user);
    }

    public Collection<User> findAll() {
        log.info("Service layer: get all users.");

        return userStorage.findAllUsers();
    }

    public User getUserById(Long id) {
        log.info("Service layer: get user with id: '{}'.", id);
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }

        return userStorage.getUserById(id);
    }

    public User deleteUser(User user) {
        if (user == null) {

            throw new IncorrectParameterException("'id' parameter equals to null.");

        }
        log.info("Service layer: delete user with id: '{}'.", user.getId());

        return userStorage.deleteUser(user);
    }

    public User addUserToFriends(Long id, Long friendId) {
        log.info("Service layer: add friend with id: '{}' to user with id: '{}'.", friendId, id);
        User user = userStorage.addUserToFriends(id, friendId);
        feedStorage.addFeedList(id, friendId, EventType.FRIEND, Operation.ADD);

        return user;
    }

    public User deleteUserFromFriend(Long id, Long friendId) {
        log.info("Service layer: delete friend with id: '{}' from user with id: '{}'.", friendId, id);
        User user = userStorage.deleteUserFromFriend(id, friendId);
        feedStorage.addFeedList(id, friendId, EventType.FRIEND, Operation.REMOVE);

        return user;
    }

    public Optional<List<User>> getCommonFriends(Long id, Long otherId) {
        log.info("Service layer: get common friends for users with id: '{}' and '{}'.", id, otherId);

        return userStorage.getCommonFriends(id, otherId);
    }

    public Collection<User> getFriendsOfUser(Long id) {
        log.info("Service layer: get friends of user with id: '{}'.", id);

        return userStorage.getFriendsOfUser(id);
    }

    public Collection<Feed> getFeed(Long id) {
        log.info("Service layer: get feed for user with id: '{}'.", id);

        return feedStorage.getFeedList(id);
    }

    public Optional<List<Film>> getRecommendationsFilms(Long id) {
        log.info("Service layer: get recommendations for film with id: '{}'.", id);

        return userStorage.getRecommendationsFilms(id);
    }
}