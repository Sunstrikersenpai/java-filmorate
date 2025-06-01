package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;

public class FilmSearchByValidator implements ConstraintValidator<FilmSearchBy, String> {
    private static final Set<String> VALID_CRITERIA = Set.of("director", "title");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .allMatch(VALID_CRITERIA::contains);
    }
}
