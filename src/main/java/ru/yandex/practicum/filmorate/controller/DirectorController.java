package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    //    Список всех режиссёров
    @GetMapping
    public ResponseEntity<List<Director>> getAllDirectors() {
        log.warn("GET /directors");
        List<Director> directorList = directorService.getAllDirectors();
        return ResponseEntity.ok(directorList);
    }

    //    Получение режиссёра по id
    @GetMapping("{id}")
    public Director getDirectorByID(@PathVariable("id") Long directorID) {
        log.info("GET /directors/{}", directorID);

        Director director = directorService.getDirectorByID(directorID); // если не найден - выбросит 404
        return director;

    }

    //  Создание режиссёра
    @PostMapping
    public Director addDirector(@RequestBody @Valid Director director) {
        log.warn("POST /directors");
        return directorService.addDirector(director);
    }

    //  Изменение режиссёра
    @PutMapping
    public ResponseEntity<Director> updateDirector(@RequestBody @Valid Director director) {
        log.info("PUT /directors");
        try {
            Director updated = directorService.updateDirector(director);
            return ResponseEntity.ok(updated);
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    //  Удаление режиссёра
    @DeleteMapping("{id}")
    public void deleteDirectors(@PathVariable("id") Long directorID) {
        log.info("DELETE /directors/{}", directorID);
        directorService.deleteDirectors(directorID);
    }

}
