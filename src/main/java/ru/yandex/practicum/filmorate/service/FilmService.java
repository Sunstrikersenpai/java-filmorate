package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.storage.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final EventDbStorage eventDbStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaDbStorage mpaDbStorage,
            GenreDbStorage genreDbStorage,
            EventDbStorage eventDbStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.eventDbStorage = eventDbStorage;
    }

    public Film create(Film film) {
        if (film.getMpa() != null) {
            validateMpa(film);
        }
        validateGenres(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (filmStorage.getFilm(film.getId()).isEmpty()) {
            throw new NotFoundException("Film not found");
        }
        if (film.getMpa() != null) {
            validateMpa(film);
        }
        validateGenres(film);
        return filmStorage.update(film);
    }

    public Film addLike(Long filmId, Long userId) {
        getUserById(userId);
        filmStorage.addLike(filmId, userId);
        logEvent(userId, filmId, EventType.LIKE, EventOperation.ADD);
        return filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    public Film deleteLike(Long filmId, Long userId) {
        getUserById(userId);
        filmStorage.deleteLike(filmId, userId);
        logEvent(userId, filmId, EventType.LIKE, EventOperation.REMOVE);
        return filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    public List<Film> getPopular(Long count) {
        return filmStorage.getPopular(count);
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
        if (film.getMpa() != null && !mpaDbStorage.existsById(film.getMpa().getId())) {
            throw new NotFoundException("MPA не найден");
        }
    }

    private void validateGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        Set<Integer> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        Set<Integer> existingGenreIds = genreDbStorage.findExistingIds(genreIds);
        if (!existingGenreIds.containsAll(genreIds)) {
            throw new NotFoundException("Жанр не найден");
        }
    }

    private void logEvent(Long userId, Long entityId, EventType type, EventOperation operation) {
        eventDbStorage.addEvent(
                Event.builder()
                        .userId(userId)
                        .entityId(entityId)
                        .eventType(type)
                        .operation(operation)
                        .build()
        );
    }

    private User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User с ID " + userId + " не найден"));
    }

    public void removeFilmById(Long filmId) {
        filmStorage.removeFilmById(filmId);
    }

}