package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.FilmSearchBy;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;
    private final DirectorService directorService;

    @GetMapping
    public List<Film> getFilms() {
        log.info(" ******** GET /films");
        return filmService.findAll();
    }

    @GetMapping("/popular")
    public List<Film> getPopular(
            @RequestParam(name = "count", defaultValue = "10") Long count,
            @RequestParam(name = "genreId", required = false) Long genreId,
            @RequestParam(name = "year", required = false) Long year
    ) {
        log.info(" ******** GET /films/popular count={}, genreId={}, year={}", count, genreId, year);
        return filmService.getPopular(count, genreId, year);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info(" ******** POST /films");
        return filmService.create(film);
    }

    @PutMapping("{id}/like/{userId}")
    public void likeFilm(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        log.info(" ******** PUT /films/{}/like/{}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        log.info(" ******** DEL /films/{}/like/{}", filmId, userId);
        filmService.deleteLike(filmId, userId);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info(" ******** PUT /films");
        return filmService.update(film);
    }

    @GetMapping("{id}")
    public Film getFilmById(
            @PathVariable("id") Long filmId
    ) {
        log.info(" ******** GET films/{}", filmId);
        return filmService.getFilmById(filmId);
    }

    @DeleteMapping("{filmId}")
    public void removeFilmById(@PathVariable("filmId") Long filmId) {
        log.info(" ******** DEL /films/{}", filmId);
        filmService.removeFilmById(filmId);
    }

    //    список фильмов режиссера отсортированных по количеству лайков или году выпуска.
    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsOfDirectorSortedByParams(
            @PathVariable("directorId") Long directorID,
            @RequestParam(required = false) String sortBy
    ) {
        log.info(" ******** GET /films/director/{}?sortBy={}", directorID, sortBy);
        return directorService.getFilmsOfDirectorSortedByParams(directorID, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam Long userId,
            @RequestParam Long friendId
    ) {
        log.info(" ******** GET /films/common");
        return filmService.getCommonFilms(userId, friendId);
    }

    // поиск по названию фильмов и по режиссёру
    @GetMapping("/search")
    public List<Film> searchFilms(
            @RequestParam String query,
            @RequestParam @FilmSearchBy String by
    ) {
        log.info(" ******** GET /films/search?query={}&by={}", query, by);
        Set<String> searchCriteria = Arrays.stream(by.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        return directorService.getFilmsBySearchCriteria(query.toLowerCase(), searchCriteria);
    }

}