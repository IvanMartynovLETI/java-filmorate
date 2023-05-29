package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private Set<Long> friendsIds = new HashSet<>();

    @Email(message = "Incorrect email")
    private String email;

    @NotNull(message = "User's login shouldn't be an empty.")
    @NotBlank(message = "User's login shouldn't be blank.")
    private String login;
    private String name;

    @Past(message = "User's date of birth should be correct.")
    private LocalDate birthday;

    public void addUserToFriend(User user) {
        friendsIds.add(user.getId());
    }

    public void deleteUserFromFriend(User user) {
        friendsIds.remove(user.getId());
    }
}