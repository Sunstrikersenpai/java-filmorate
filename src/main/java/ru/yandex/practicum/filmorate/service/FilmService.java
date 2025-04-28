package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(Long filmId, Long userId) {
        getUserById(userId);
        Film film = filmStorage.getFilm(filmId);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        film.getUsersLikes().add(userId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        getUserById(userId);
        Film film = filmStorage.getFilm(filmId);
        film.getUsersLikes().remove(userId);
        return film;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("User with id " + id + "not found"));
    }

    public List<Film> getTopFilms(int count) {
        List<Film> sortedFilms = filmStorage.findAll();
        sortedFilms.sort(Comparator.comparing((Film film) -> film.getUsersLikes().size()).reversed());

        return sortedFilms.subList(0, Math.min(count, sortedFilms.size()));
    }
}
