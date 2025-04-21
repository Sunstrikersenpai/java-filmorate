package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendList(@PathVariable("id") Long userId) {
        return userService.getFriendList(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable("id") Long userId,
            @PathVariable("otherId") Long otherId
    ) {
    return userService.showCommonFriendsList(userId,otherId);
    }

    @PutMapping("{id}/friends/{friendId}")
    public User addUserToFriendList(
            @PathVariable("id") Long user1Id,
            @PathVariable("friendId") Long user2Id
    ) {
        return userService.addUserToFriendList(user1Id, user2Id);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public User deleteUserFromFriendList(
            @PathVariable("id") Long user1Id,
            @PathVariable("friendId") Long user2Id
    ) {
        return userService.deleteUserFromFriendList(user1Id, user2Id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        return userStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        return userStorage.updateUser(user);
    }
}
