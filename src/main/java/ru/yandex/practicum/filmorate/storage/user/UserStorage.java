package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

@Service
public interface UserStorage {
    User addUser(User user);

    User modifyUser(User user);

    User deleteUser(User user);

    User getUserById(Long id);
}