package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Director>> getAllDirectors() {
        log.warn(" ******** GET /directors");
        List<Director> directorList = directorService.getAllDirectors();

// Status code is 200 | AssertionError: expected response to have status reason 'OK' but got 'NO CONTENT'
//        if (directorList.isEmpty()) {
//            return ResponseEntity.ok().build();
//        }
        return ResponseEntity.ok(directorList);
    }

    //    Получение режиссёра по id
    @GetMapping("{id}")
    public ResponseEntity<Director> getDirectorByID(@PathVariable("id") Long directorID) {
        log.info(" ******** GET /directors/{}", directorID);

        Director director = directorService.getDirectorByID(directorID); // если не найден - выбросит 404
        return ResponseEntity.ok(director);

    }

    //  Создание режиссёра
    @PostMapping
    public Director addDirector(@RequestBody @Valid Director director) {
        log.warn(" ******** POST /directors");
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
