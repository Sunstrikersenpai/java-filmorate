package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    void removeUserById(Long userId);
}