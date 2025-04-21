package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyLikedException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage films;

    @Autowired
    public FilmService(FilmStorage films) {
        this.films = films;
    }

    public Film addLike(Long filmId, Long userId) {
        Film film = films.getFilm(filmId);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        film.getUsersLikes().add(userId);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        Film film = films.getFilm(filmId);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        film.getUsersLikes().remove(userId);
        return film;
    }

    public List<Film> getTopFilms(int count) {
        List<Film> sortedFilms = films.findAll();
        sortedFilms.sort(Comparator.comparing((Film film) -> film.getUsersLikes().size()).reversed());
        
        return sortedFilms.size() > count ? sortedFilms.subList(0,count):sortedFilms;
    }
}
