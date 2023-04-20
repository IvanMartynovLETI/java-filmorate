package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;

    @Email(message = "Incorrect email")
    private String email;

    @NotNull(message = "User's login shouldn't be an empty.")
    @NotBlank(message = "User's login shouldn't be blank.")
    private String login;
    private String name;

    @Past(message = "User's date of birth should be correct.")
    private LocalDate birthday;
}