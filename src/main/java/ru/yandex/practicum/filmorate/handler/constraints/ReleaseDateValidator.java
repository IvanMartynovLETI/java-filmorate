package ru.yandex.practicum.filmorate.handler.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    LocalDate birthOfCinemaDate = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date != null) {
            return date.isAfter(birthOfCinemaDate);
        }
        return true;
    }
}