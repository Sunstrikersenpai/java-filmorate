package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getDirectorList();

    Optional<Director> getDirectorByID(Long directorID);

    Director add(Director director);

    Director update(Director director);

    void delete(Long directorID);
}
