package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
        log.info("Request for user with id: {} creation obtained.", user.getId());

        User checkedPeople = user;
        try {
            checkedPeople = validator.validatePeople(user, users, true);
            users.put(checkedPeople.getId(), checkedPeople);
        } catch (ValidationException e) {
            log.warn(e.getMessage());
        }

        return checkedPeople;
    }

    @PutMapping
    public ResponseEntity<User> put(@RequestBody User user) {
        log.info("Request for film with id: {} putting obtained.", user.getId());
        HttpStatus status = HttpStatus.OK;
        User checkedUser = user;
        try {
            checkedUser = validator.validatePeople(user, users, false);
            users.put(checkedUser.getId(), checkedUser);
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            status = validator.getStatus(e.getMessage());
        }

        return ResponseEntity.status(status).body(checkedUser);
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Request for getting user's collection obtained.");
        return users.values();
    }
}