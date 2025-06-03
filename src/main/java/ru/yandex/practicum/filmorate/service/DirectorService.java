package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FilmSortBy;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class DirectorService {

    private final DirectorDbStorage directorsDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorsDbStorage, FilmDbStorage filmDbStorage) {
        this.directorsDbStorage = directorsDbStorage;
        this.filmDbStorage = filmDbStorage;
    }

    //    Список всех режиссёров
    public List<Director> getAllDirectors() {
        return directorsDbStorage.getDirectorList();
    }

    //    Получение режиссёра по id
    public Director getDirectorByID(Long directorID) {
        return directorsDbStorage.getDirectorByID(directorID)
                .orElseThrow(() -> new NotFoundException("Режиссёр с ID " + directorID + " не найден"));
    }

    //  Создание режиссёра
    public Director addDirector(Director directors) {


        try {
            return directorsDbStorage.add(directors);
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

        return directorsDbStorage.update(directors);
    }

    //  Удаление режиссёра
    public void deleteDirectors(Long directorID) {
        directorsDbStorage.delete(directorID);
    }

    //    список фильмов режиссера отсортированных по количеству лайков или году выпуска.
    public List<Film> getFilmsOfDirectorSortedByParams(Long directorID, FilmSortBy sortBy) {
        // Проверка наличия режиссёра (выбросит NotFoundException, если не найден)
        getDirectorByID(directorID);
        return filmDbStorage.getFilmsOfDirectorSortedByParams(directorID, sortBy);
    }

    // поиск по названию фильмов и по режиссёру
    public List<Film> getFilmsBySearchCriteria(String query, Set<String> searchCriteria) {
        return filmDbStorage.getFilmsBySearchCriteria(query, searchCriteria);
    }
}
