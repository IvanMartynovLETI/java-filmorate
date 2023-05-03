package ru.yandex.practicum.filmorate.storage.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Component
@Data
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
    public User deleteUser(User user) {
        log.info("Request for user with id '{}' deletion obtained.", user.getId());
        User checkedPeople = validator.validateUser(user, users, false);
        users.remove(checkedPeople.getId());

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
    public User getUserById(Long id) {
        log.info("Request for obtaining user by id: {} obtained.", id);
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("User with id: " + id + " not found.");
        }

        return users.get(id);
    }

    public Collection<User> findAllUsers() {
        log.info("Request for getting user's collection obtained. Now {} users present.", users.size());

        return users.values();
    }
}