package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public List<Director> getAllDirectors() {
        log.info(" ******** GET /directors");
        return directorService.getAllDirectors();
    }

    //    Получение режиссёра по id
    @GetMapping("{id}")
    public Director getDirectorByID(@PathVariable("id") Long directorID) {
        log.info(" ******** GET /directors/{}", directorID);
        return directorService.getDirectorByID(directorID);
    }

    //  Создание режиссёра
    @PostMapping
    public Director addDirector(@RequestBody @Valid Director director) {
        log.info(" ******** POST /directors");
        return directorService.addDirector(director);
    }

    //  Изменение режиссёра
    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        log.info(" ******** PUT /directors");
        return directorService.updateDirector(director);
    }

    //  Удаление режиссёра
    @DeleteMapping("{id}")
    public void deleteDirectors(@PathVariable("id") Long directorID) {
        log.info(" ******** DELETE /directors/{}", directorID);
        directorService.deleteDirectors(directorID);
    }

}
