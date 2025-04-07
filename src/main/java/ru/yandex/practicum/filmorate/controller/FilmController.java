package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate earliestDate = LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        validateFilmReleaseDate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film uptFilm(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с ID {} не найден", film.getId());
            throw new IllegalArgumentException();
        }
        validateFilmReleaseDate(film);
        films.put(film.getId(), film);
        return film;
    }

    private void validateFilmReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(earliestDate)) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    public int getNextId() {
        int currentMaxId = films.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
