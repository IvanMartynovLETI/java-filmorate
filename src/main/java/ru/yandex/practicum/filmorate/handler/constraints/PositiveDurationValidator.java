package ru.yandex.practicum.filmorate.handler.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public class PositiveDurationValidator implements ConstraintValidator<PositiveDuration, Duration> {

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext context) {
        if (duration != null) {
            return !duration.isNegative();
        }
        return true;
    }
}