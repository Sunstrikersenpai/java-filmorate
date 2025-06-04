package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmSortBy;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    //    Список всех режиссёров
    public List<Director> getAllDirectors() {
        return directorStorage.getDirectorList();
    }

    //    Получение режиссёра по id
    public Director getDirectorByID(Long directorID) {
        return directorStorage.getDirectorByID(directorID)
                .orElseThrow(() -> new NotFoundException("Режиссёр с ID " + directorID + " не найден"));
    }

    //  Создание режиссёра
    public Director addDirector(Director director) {

        try {
            return directorStorage.add(director);
        } catch (IllegalArgumentException e) {
            // Логируем ошибку валидации
            log.error("Ошибка при создании режиссёра: {}", e.getMessage());
            throw new ValidationException(e.getMessage());
        }
    }

    //  Изменение режиссёра
    public Director updateDirector(Director directors) {
        // Проверка наличия режиссёра (выбросит NotFoundException, если не найден)
        getDirectorByID(directors.getId());

        return directorStorage.update(directors);
    }

    //  Удаление режиссёра
    public void deleteDirectors(Long directorID) {
        directorStorage.delete(directorID);
    }

    //    список фильмов режиссера отсортированных по количеству лайков или году выпуска.
    public List<Film> getFilmsOfDirectorSortedByParams(Long directorID, String sortBy) {
        // Проверка наличия режиссёра (выбросит NotFoundException, если не найден)
        getDirectorByID(directorID);

        FilmSortBy sortCriteria = null;
        if (sortBy != null && !sortBy.isBlank()) {
            sortCriteria = FilmSortBy.fromString(sortBy);
        }

        return filmStorage.getFilmsOfDirectorSortedByParams(directorID, sortCriteria);
    }

    // поиск по названию фильмов и по режиссёру
    public List<Film> getFilmsBySearchCriteria(String query, Set<String> searchCriteria) {
        return filmStorage.getFilmsBySearchCriteria(query, searchCriteria);
    }
}
