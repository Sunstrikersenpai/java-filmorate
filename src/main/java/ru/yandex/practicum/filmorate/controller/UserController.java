package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.info("GET /users");
        return userService.getUsers();
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendList(@PathVariable("id") Long userId) {
        log.info("GET {}/friends", userId);
        return userService.getFriendList(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable("id") Long userId,
            @PathVariable("otherId") Long otherId
    ) {
        log.info("GET {}/friends/common/{}", userId, otherId);
        return userService.showCommonFriendsList(userId, otherId);
    }

    @PutMapping("{id}/friends/{friendId}")
    public User addUserToFriendList(
            @PathVariable("id") Long user1Id,
            @PathVariable("friendId") Long user2Id
    ) {
        log.info("PUT {}/friends/{}", user1Id, user2Id);
        return userService.addUserToFriendList(user1Id, user2Id);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public User deleteUserFromFriendList(
            @PathVariable("id") Long user1Id,
            @PathVariable("friendId") Long user2Id
    ) {
        log.info("DEL {}/friends/{}", user1Id, user2Id);
        return userService.deleteUserFromFriendList(user1Id, user2Id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        log.info("POST /users");
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        log.info("PUT /users");
        return userService.updateUser(user);
    }

    @GetMapping("/{id}/feed")
    public List<Event> getFeed(@PathVariable Long id) {
        return userService.getFeed(id);
    }
}