package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

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

    public User addUserToFriends(Long id, Long friendId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (friendId == null) {
            throw new IncorrectParameterException("'friendId' parameter equals to null.");
        }

        User user = userStorage.getUserById(id);
        User userToBeAddedAsFriend = userStorage.getUserById(friendId);

        log.info("User with id: {} wants to add user with id: {} as friend.", user.getId(),
                userToBeAddedAsFriend.getId());

        user.addUserToFriend(userToBeAddedAsFriend);
        userToBeAddedAsFriend.addUserToFriend(user);
        userStorage.modifyUser(user);
        userStorage.modifyUser(userToBeAddedAsFriend);

        return user;
    }

    public User deleteUserFromFriend(Long id, Long friendId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (friendId == null) {
            throw new IncorrectParameterException("'friendId' parameter equals to null.");
        }

        User user = userStorage.getUserById(id);
        User userToBeDeletedFromFriends = userStorage.getUserById(friendId);

        log.info("User with id: {} wants to delete user with id: {} from friends.", user.getId(),
                userToBeDeletedFromFriends.getId());

        user.deleteUserFromFriend(userToBeDeletedFromFriends);
        userToBeDeletedFromFriends.deleteUserFromFriend(user);
        userStorage.modifyUser(user);
        userStorage.modifyUser(userToBeDeletedFromFriends);

        return user;
    }

    public Optional<List<User>> getCommonFriends(Long id, Long otherId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (otherId == null) {
            throw new IncorrectParameterException("'otherId' parameter equals to null.");
        }

        User user = userStorage.getUserById(id);
        User otherUser = userStorage.getUserById(otherId);

        log.info("Obtaining common friends for user with id: {} and user with id: {}", user.getId(), otherUser.getId());
        List<User> commonFriends = new ArrayList<>();
        if (user.getFriendsIds() == null || otherUser.getFriendsIds() == null) {
            return Optional.of(commonFriends);
        }

        Set<Long> friendsIdsForUser1 = new HashSet<>(user.getFriendsIds());
        friendsIdsForUser1.retainAll(otherUser.getFriendsIds());

        Map<Long, User> users = userStorage.findAllUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        commonFriends = friendsIdsForUser1.stream()
                .filter(users::containsKey)
                .map(users::get)
                .collect(Collectors.toList());

        return Optional.of(commonFriends);
    }

    public Collection<User> getFriendsOfUser(Long id) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }

        User user = userStorage.getUserById(id);

        log.info("Obtaining list of friends for user with id: {}.", user.getId());

        Map<Long, User> users = userStorage.findAllUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return user.getFriendsIds().stream()
                .filter(users::containsKey)
                .map(users::get)
                .collect(Collectors.toList());
    }
}