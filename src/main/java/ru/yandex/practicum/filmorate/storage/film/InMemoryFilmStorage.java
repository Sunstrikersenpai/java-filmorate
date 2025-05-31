package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        films.put(film.getId(), film);
        return film;
    }

    public Optional<Film> getFilm(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        film.getUsersLikes().add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        films.keySet().forEach(System.out::println);
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film not found");
        }
        film.getUsersLikes().remove(userId);
    }

    @Override
    public List<Film> getPopular(Long count, Long genreId, Long year) {
        return findAll().stream()
                .filter(film -> genreId == null ||
                        (film.getGenres() != null && film.getGenres().stream()
                                .anyMatch(genre -> genre.getId() != null && genre.getId().longValue() == genreId.longValue())))
                .filter(film -> year == null || film.getReleaseDate() != null && film.getReleaseDate().getYear() == year)
                .sorted((f1, f2) -> Integer.compare(
                        f2.getUsersLikes().size(),
                        f1.getUsersLikes().size()
                ))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFilmById(Long filmId) {
        films.remove(filmId);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return List.of();
    }
}