package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FilmSearchByValidator.class)
public @interface FilmSearchBy {
    String message() default "Invalid search criteria";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}