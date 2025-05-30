package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        log.info("GET /films");
        return filmService.findAll();
    }

    @GetMapping("popular")
    public List<Film> getPopular(@RequestParam(name = "count", defaultValue = "10") Long count) {
        log.info("GET /popular par count = {}", count);
        return filmService.getPopular(count);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("POST /films");
        return filmService.create(film);
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
        return filmService.update(film);
    }

    @GetMapping("{id}")
    public Film getFilmById(
            @PathVariable("id") Long filmId
    ) {
        log.info("GET films/{}", filmId);
        return filmService.getFilmById(filmId);
    }
}