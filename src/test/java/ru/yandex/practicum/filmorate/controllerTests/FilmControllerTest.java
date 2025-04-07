package ru.yandex.practicum.filmorate.controllerTests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    FilmController controller = new FilmController();
    Film film;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Film")
                .description("Description")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .build();
    }

    @Test
    void addFilm_successfully() {
        controller.addFilm(film);

        assertTrue(controller.getFilms().contains(film));
        assertEquals(1, film.getId());
    }

    @Test
    void uptFilm_successfully() {
        controller.addFilm(film);
        Film updatedFilm = film.toBuilder().name("Interstellar").build();
        controller.uptFilm(updatedFilm);

        assertEquals(1, controller.getFilms().size());
        assertNotEquals(controller.getFilms().getFirst(), film);
        assertEquals("Interstellar", controller.getFilms().getFirst().getName());
    }

    @Test
    void addFilm_blankNameThrows() {
        Film invalid = film.toBuilder().name(" ").build();
        Set<ConstraintViolation<Film>> violations = validator.validate(invalid);

        assertFalse(violations.isEmpty());
    }

    @Test
    void addFilm_descriptionTooLongThrows() {
        String tooLongDesc = "a".repeat(201);
        Film invalid = film.toBuilder().description(tooLongDesc).build();
        Set<ConstraintViolation<Film>> violations = validator.validate(invalid);

        assertFalse(violations.isEmpty());
    }

    @Test
    void addFilm_invalidReleaseDateThrows() {
        Film invalid = film.toBuilder().releaseDate(LocalDate.of(1800, 1, 1)).build();

        assertThrows(ValidationException.class, () -> controller.addFilm(invalid));
        assertTrue(controller.getFilms().isEmpty());
    }

    @Test
    void addFilm_negativeDurationThrows() {
        Film invalid = film.toBuilder().duration(-120).build();

        assertTrue(controller.getFilms().isEmpty());
    }

    @Test
    void uptFilm_filmNotFoundThrows() {
        controller.addFilm(film);
        Film other = film.toBuilder().id(999).name("Unknown").build();

        assertThrows(IllegalArgumentException.class, () -> controller.uptFilm(other));
    }

    @Test
    void getFilms_returnsAll() {
        Film second = film.toBuilder().name("The Matrix").build();

        controller.addFilm(film);
        controller.addFilm(second);

        assertEquals(2, controller.getFilms().size());
        assertTrue(controller.getFilms().contains(film));
        assertTrue(controller.getFilms().contains(second));
    }
}
