package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;
    private final Validator validator;

    @Override
    public User addUser(User user) {
        log.info("Request for user with login '{}' creation obtained.", user.getLogin());
        User checkedPeople = validator.validateUser(user, users, true);
        users.put(checkedPeople.getId(), checkedPeople);

        return checkedPeople;
    }

    @Override
    public User modifyUser(User user) {
        log.info("Request for user with id '{}' update obtained.", user.getId());
        User checkedUser = validator.validateUser(user, users, false);
        users.put(checkedUser.getId(), checkedUser);

        return checkedUser;
    }

    @Override
    public User deleteUser(User user) {
        log.info("Request for user with id '{}' deletion obtained.", user.getId());
        User checkedPeople = validator.validateUser(user, users, false);
        users.remove(checkedPeople.getId());

        return checkedPeople;
    }

    @Override
    public User getUserById(Long id) {
        log.info("Request for obtaining user by id: {} obtained.", id);
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException("User with id: " + id + " not found.");
        }

        return users.get(id);
    }

    @Override
    public Collection<User> findAllUsers() {
        log.info("Request for getting user's collection obtained. Now {} users present.", users.size());

        return users.values();
    }

    @Override
    public User addUserToFriends(Long id, Long friendId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (friendId == null) {
            throw new IncorrectParameterException("'friendId' parameter equals to null.");
        }

        User user = getUserById(id);
        User userToBeAddedAsFriend = getUserById(friendId);

        log.info("User with id: {} wants to add user with id: {} as friend.", user.getId(),
                userToBeAddedAsFriend.getId());

        user.addUserToFriend(userToBeAddedAsFriend);
        userToBeAddedAsFriend.addUserToFriend(user);
        modifyUser(user);
        modifyUser(userToBeAddedAsFriend);

        return user;
    }

    @Override
    public User deleteUserFromFriend(Long id, Long friendId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (friendId == null) {
            throw new IncorrectParameterException("'friendId' parameter equals to null.");
        }

        User user = getUserById(id);
        User userToBeDeletedFromFriends = getUserById(friendId);

        log.info("User with id: {} wants to delete user with id: {} from friends.", user.getId(),
                userToBeDeletedFromFriends.getId());

        user.deleteUserFromFriend(userToBeDeletedFromFriends);
        userToBeDeletedFromFriends.deleteUserFromFriend(user);
        modifyUser(user);
        modifyUser(userToBeDeletedFromFriends);

        return user;
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

        log.info("Obtaining common friends for user with id: {} and user with id: {}", id, otherId);
        List<User> commonFriends = new ArrayList<>();
        if (user.getFriendsIds() == null || otherUser.getFriendsIds() == null) {
            return Optional.of(commonFriends);
        }

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

        log.info("Obtaining list of friends for user with id: {}.", user.getId());

        Map<Long, User> users = findAllUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return user.getFriendsIds().stream()
                .filter(users::containsKey)
                .map(users::get)
                .collect(Collectors.toList());
    }
}