package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

        return userStorage.addUserToFriends(id, friendId);
    }

    public User deleteUserFromFriend(Long id, Long friendId) {

        return userStorage.deleteUserFromFriend(id, friendId);
    }

    public Optional<List<User>> getCommonFriends(Long id, Long otherId) {

        return userStorage.getCommonFriends(id, otherId);
    }

    public Collection<User> getFriendsOfUser(Long id) {

        return userStorage.getFriendsOfUser(id);
    }

    public Optional<List<Film>> getRecommendationsFilms(Long id){
        return userStorage.getRecommendationsFilms(id);
    }
}