package ru.yandex.practicum.filmorate.service.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class UserService {
    private Validator validator;
    private final UserStorage userStorage;

    public User addUserToFriends(User user, User userToBeAddedAsFriend) {
        log.info("User with id: {} wants to add user with id: {} as friend.", user.getId(),
                userToBeAddedAsFriend.getId());

        user.addUserToFriend(userToBeAddedAsFriend);
        userToBeAddedAsFriend.addUserToFriend(user);
        userStorage.modifyUser(user);
        userStorage.modifyUser(userToBeAddedAsFriend);

        return user;
    }

    public User deleteUserFromFriend(User user, User userToBeDeletedFromFriends) {
        log.info("User with id: {} wants to delete user with id: {} from friends.", user.getId(),
                userToBeDeletedFromFriends.getId());

        user.deleteUserFromFriend(userToBeDeletedFromFriends);
        userToBeDeletedFromFriends.deleteUserFromFriend(user);
        userStorage.modifyUser(user);
        userStorage.modifyUser(userToBeDeletedFromFriends);

        return user;
    }

    public Optional<List<User>> getCommonFriends(User user1, User user2) {
        log.info("Obtaining common friends for user with id: {} and user with id: {}", user1.getId(), user2.getId());
        List<User> commonFriends = new ArrayList<>();
        if (user1.getFriendsIds() == null || user2.getFriendsIds() == null) {
            return Optional.of(commonFriends);
        }

        Set<Long> friendsIdsForUser1 = new HashSet<>(user1.getFriendsIds());
        friendsIdsForUser1.retainAll(user2.getFriendsIds());

        commonFriends = friendsIdsForUser1.stream().filter(p -> ((InMemoryUserStorage) userStorage).
                        getUsers().containsKey(p)).map(p -> ((InMemoryUserStorage) userStorage).getUsers().get(p)).
                collect(Collectors.toList());

        return Optional.of(commonFriends);
    }

    public Collection<User> getFriendsOfUser(User user) {
        log.info("Obtaining list of friends for user with id: {}.", user.getId());

        return user.getFriendsIds().stream().filter(p -> ((InMemoryUserStorage) userStorage).getUsers().containsKey(p))
                .map(p -> ((InMemoryUserStorage) userStorage).getUsers().get(p)).collect(Collectors.toList());
    }
}