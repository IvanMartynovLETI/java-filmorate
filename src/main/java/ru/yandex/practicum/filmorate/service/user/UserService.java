package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    public User addUser(User user) {

        return userStorage.addUser(user);

    }

    public User modifyUser(User user) {

        return userStorage.modifyUser(user);

    }

    public Collection<User> findAll() {

        return userStorage.findAllUsers();

    }

    public User getUserById(Long id) {

        if (id == null) {

            throw new IncorrectParameterException("'id' parameter equals to null.");

        }

        return userStorage.getUserById(id);
    }

    public User deleteUser(User user) {

        if (user == null) {

            throw new IncorrectParameterException("'id' parameter equals to null.");

        }
        return userStorage.deleteUser(user);
    }

    public User addUserToFriends(Long id, Long friendId) {
        User user = userStorage.addUserToFriends(id, friendId);
        feedStorage.addFeedList(id,friendId, EventType.FRIEND, Operation.ADD);
        return user;
    }

    public User deleteUserFromFriend(Long id, Long friendId) {
        User user = userStorage.deleteUserFromFriend(id, friendId);
        feedStorage.addFeedList(id,friendId, EventType.FRIEND, Operation.REMOVE);
        return user;
    }

    public Optional<List<User>> getCommonFriends(Long id, Long otherId) {

        return userStorage.getCommonFriends(id, otherId);
    }

    public Collection<User> getFriendsOfUser(Long id) {

        return userStorage.getFriendsOfUser(id);
    }

    public Collection<Feed> getFeed(Long id) {

        return feedStorage.getFeedList(id);
    }
}