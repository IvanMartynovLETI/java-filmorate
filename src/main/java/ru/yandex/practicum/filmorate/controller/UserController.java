package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final Validator validator = new Validator();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Request for user with login '{}' creation obtained.", user.getLogin());
        User checkedPeople = validator.validatePeople(user, users, true);
        users.put(checkedPeople.getId(), checkedPeople);

        return checkedPeople;
    }

    @PutMapping
    public User put(@RequestBody User user) {
        log.info("Request for user with id '{}' putting obtained.", user.getId());
        User checkedUser = validator.validatePeople(user, users, false);
        users.put(checkedUser.getId(), checkedUser);

        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Request for getting user's collection obtained. Now {} users present.", users.size());
        return users.values();
    }
}