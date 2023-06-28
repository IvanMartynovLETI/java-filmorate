package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class Director implements Comparable<Director> {
    private Long id;
    @NotNull(message = "User's login shouldn't be an empty.")
    @NotBlank(message = "User's login shouldn't be blank.")
    private String name;

    @Override
    public int compareTo(Director otherDirector) {
        return this.getId().compareTo(otherDirector.getId());
    }
}