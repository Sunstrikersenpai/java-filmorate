package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

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
    public List<Film> getPopular(Long count) {
        List<Film> sortedFilms = findAll();
        sortedFilms.sort(Comparator.comparing((Film film) -> film.getUsersLikes().size()).reversed());

        return sortedFilms.subList(0, Math.toIntExact(Math.min(count, sortedFilms.size())));
    }
}