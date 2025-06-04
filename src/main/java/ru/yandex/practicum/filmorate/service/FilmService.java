package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final EventService eventService;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaStorage mpaStorage,
            GenreStorage genreStorage,
            EventService eventService
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.eventService = eventService;
    }

    public Film create(Film film) {
        if (film.getMpa() != null) {
            validateMpa(film);
        }
        validateGenres(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        getFilmById(film.getId());
        if (film.getMpa() != null) {
            validateMpa(film);
        }
        validateGenres(film);
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        getUserById(userId);
        getFilmById(filmId);
        if (!filmStorage.likeExists(filmId, userId)) {
            filmStorage.addLike(filmId, userId);
        }
        //Ивент должен логироваться в любом случае для теста
        eventService.logEvent(userId, filmId, EventType.LIKE, EventOperation.ADD);
    }

    public void deleteLike(Long filmId, Long userId) {
        getUserById(userId);
        getFilmById(filmId);
        filmStorage.deleteLike(filmId, userId);
        eventService.logEvent(userId, filmId, EventType.LIKE, EventOperation.REMOVE);
    }

    public List<Film> getPopular(Long count, Long genreId, Long year) {
        return filmStorage.getPopular(count, genreId, year);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilm(id).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    private void validateMpa(Film film) {
        if (film.getMpa() != null && !mpaStorage.existsById(film.getMpa().getId())) {
            throw new NotFoundException("MPA не найден");
        }
    }

    private void validateGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        List<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

        List<Integer> existingGenreIds = genreStorage.findExistingIds(genreIds);
        if (!existingGenreIds.containsAll(genreIds)) {
            throw new NotFoundException("Жанр не найден");
        }
    }

    private User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User с ID " + userId + " не найден"));
    }

    public void removeFilmById(Long filmId) {
        filmStorage.removeFilmById(filmId);
    }

    public List<Film> getRecommendationsFilms(Long userId) {
        return filmStorage.getRecommendationsFilms(userId);
    }
}