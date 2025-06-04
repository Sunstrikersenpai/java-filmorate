package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmSortBy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> getFilm(Long id);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getPopular(Long count, Long genreId, Long year);

    void removeFilmById(Long filmId);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getRecommendationsFilms(Long userId);

    boolean likeExists(Long filmId, Long userId);

    List<Film> getFilmsOfDirectorSortedByParams(Long directorID, FilmSortBy sortBy);

    List<Film> getFilmsBySearchCriteria(String query, Set<String> searchCriteria);
}