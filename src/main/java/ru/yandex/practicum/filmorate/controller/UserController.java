package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor

public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@Valid
                       @RequestBody User user) {
        log.info("Request for user adding obtained.");
        return userService.addUser(user);
    }

    @PutMapping
    public User put(@RequestBody User user) {
        log.info("Request for user modification obtained.");
        return userService.modifyUser(user);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Request for receiving of all users obtained.");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public User getUserById(@Valid
                            @PathVariable(required = false) final Long id) {
        log.info("Request for getting user by id obtained.");
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public User addUserToFriend(@PathVariable(required = false) final Long id,
                                @PathVariable(required = false) final Long friendId) {
        log.info("Request for adding user to friends obtained.");
        return userService.addUserToFriends(id, friendId);
    }


    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseBody
    public User deleteUserFromFriends(@PathVariable(required = false) final Long id,
                                      @PathVariable(required = false) final Long friendId) {
        log.info("Request for deleting user from friends obtained.");
        return userService.deleteUserFromFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseBody
    public Collection<User> getFriendsOfUser(@PathVariable(required = false) final Long id) {
        log.info("Request for getting friends of user obtained.");
        return userService.getFriendsOfUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseBody
    public Optional<List<User>> getCommonFriends(@PathVariable(required = false) final Long id,
                                                 @PathVariable(required = false) final Long otherId) {
        log.info("Request for getting common friends obtained.");
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("{userId}")
    @ResponseBody
    public void deleteUser(@PathVariable("userId") long userId) {
        log.info("Пользователь " + userId + " был удален");
        userService.deleteUser(userService.getUserById(userId));
    }
}