package ru.yandex.practicum.filmorate.handler.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = PositiveDurationValidator.class)
@Documented
public @interface PositiveDuration {
    String message() default "Incorrect duration.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}