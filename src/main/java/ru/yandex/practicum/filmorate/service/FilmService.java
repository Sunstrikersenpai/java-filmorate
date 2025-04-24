package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage films;
    private final UserStorage users;

    @Autowired
    public FilmService(FilmStorage films, UserStorage users) {
        this.films = films;
        this.users = users;
    }

    public Film addLike(Long filmId, Long userId) {
        if (!users.isUserExist(userId)) {
            throw new NotFoundException("User not found");
        }
        Film film = films.getFilm(filmId);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        film.getUsersLikes().add(userId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (!users.isUserExist(userId)) {
            throw new NotFoundException("User not found");
        }
        Film film = films.getFilm(filmId);
        film.getUsersLikes().remove(userId);
        return film;
    }

    public List<Film> getTopFilms(int count) {
        List<Film> sortedFilms = films.findAll();
        sortedFilms.sort(Comparator.comparing((Film film) -> film.getUsersLikes().size()).reversed());

        return sortedFilms.subList(0, Math.min(count, sortedFilms.size()));
    }
}
