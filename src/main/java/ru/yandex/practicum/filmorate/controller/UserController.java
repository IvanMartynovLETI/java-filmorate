package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @PostMapping
    public User create(@Valid @RequestBody User user) {

        return userStorage.addUser(user);
    }

    @PutMapping
    public User put(@RequestBody User user) {

        return userStorage.modifyUser(user);
    }

    @GetMapping
    public Collection<User> findAll() {

        return ((InMemoryUserStorage) userStorage).findAllUsers();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public User getUserById(@PathVariable(required = false) final Long id) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }

        return userStorage.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public User addUserToFriend(@PathVariable(required = false) final Long id, @PathVariable(required = false) final Long friendId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (friendId == null) {
            throw new IncorrectParameterException("'friendId' parameter equals to null.");
        }

        return userService.addUserToFriends(userStorage.getUserById(id), userStorage.getUserById(friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public User deleteUserFromFriends(@PathVariable(required = false) final Long id, @PathVariable(required = false) final Long friendId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (friendId == null) {
            throw new IncorrectParameterException("'friendId' parameter equals to null.");
        }

        return userService.deleteUserFromFriend(userStorage.getUserById(id), userStorage.getUserById(friendId));
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public Collection<User> getFriendsOfUser(@PathVariable(required = false) final Long id) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }

        return userService.getFriendsOfUser(userStorage.getUserById(id));
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseBody
    public Optional<List<User>> getCommonFriends(@PathVariable(required = false) final Long id,
                                                 @PathVariable(required = false) final Long otherId) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (otherId == null) {
            throw new IncorrectParameterException("'otherId' parameter equals to null.");
        }

        return userService.getCommonFriends(userStorage.getUserById(id), userStorage.getUserById(otherId));
    }
}