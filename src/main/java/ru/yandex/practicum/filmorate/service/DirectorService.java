package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.FilmSortBy;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;

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
        return directorsDbStorage.add(directors);
    }

    //  Изменение режиссёра
    public Director updateDirector(Director directors) {
        Director checkedDirector = getDirectorByID(directors.getId());
        return directorsDbStorage.update(directors);
    }

    //  Удаление режиссёра
    public void deleteDirectors(Long directorID) {
        directorsDbStorage.delete(directorID);
    }

    //    список фильмов режиссера отсортированных по количеству лайков или году выпуска.
    public List<Film> getFilmsOfDirectorSortedByParams(Long directorID, FilmSortBy sortBy) {
        return filmDbStorage.getFilmsOfDirectorSortedByParams(directorID, sortBy);

    }


}
