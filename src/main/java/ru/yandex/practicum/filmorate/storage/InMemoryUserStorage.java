package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public List<User> getUsers() {
        return users.values().stream().toList();
    }

    public Map<Long, User> getMapUsers() {
        return users;
    }

    public User addUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь с ID {} не найден", user.getId());
            throw new NotFoundException("User not found");
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }


    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public long getNextId() {
        long currentMaxId = users.keySet().stream().mapToLong(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
