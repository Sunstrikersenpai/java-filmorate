package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("GET /films");
        return filmStorage.findAll();
    }

    @GetMapping("popular")
    public List<Film> getTopFilms(@RequestParam(name = "count", defaultValue = "10") int count) {
        log.info("GET /popular par count = {}", count);
        return filmService.getTopFilms(count);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("POST /films");
        return filmStorage.create(film);
    }

    @PutMapping("{id}/like/{userId}")
    public Film likeFilm(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        log.info("PUT {}/like/{}", filmId, userId);
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        log.info("DEL {}/like/{}", filmId, userId);
        return filmService.deleteLike(filmId, userId);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("PUT /films");
        return filmStorage.update(film);
    }
}
