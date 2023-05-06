package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User addUser(User user);

    User modifyUser(User user);

    User deleteUser(User user);

    User getUserById(Long id);

    Collection<User> findAllUsers();
}