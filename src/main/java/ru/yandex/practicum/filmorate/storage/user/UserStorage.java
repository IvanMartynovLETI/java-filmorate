package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User modifyUser(User user);

    User deleteUser(User user);

    User getUserById(Long id);

    Collection<User> findAllUsers();

    User addUserToFriends(Long id, Long friendId);

    User deleteUserFromFriend(Long id, Long friendId);

    Optional<List<User>> getCommonFriends(Long id, Long otherId);

    Collection<User> getFriendsOfUser(Long id);

}