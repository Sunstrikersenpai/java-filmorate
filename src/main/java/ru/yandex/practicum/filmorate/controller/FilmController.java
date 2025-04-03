package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate earliestDate = LocalDate.of(1895, 12, 28);
    private static final int maxDescriptionLength = 200;

    @GetMapping
    public List<Film> getFilms() {
        return films.values().stream().toList();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film uptFilm(@RequestBody @Valid Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Пользователь с ID {} не найден", film.getId());
            throw new IllegalArgumentException();
        }
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Некорректное название: {}", film.getName());
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > maxDescriptionLength) {
            log.warn("Некорректный размер описания: {}", film.getDescription().length());
            throw new ValidationException("Максимальная длинная описания 200 символов");
        }
        if (film.getReleaseDate().isBefore(earliestDate)) {
            log.warn("Некорректная дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Некорректная продолжительность: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    public int getNextId() {
        int currentMaxId = films.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
