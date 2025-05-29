package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(
            @Qualifier("userDbStorage") UserStorage userStorage
    ) {
        this.userStorage = userStorage;
    }

    public User addUserToFriendList(Long user1Id, Long user2Id) {
        User user1 = getUserById(user1Id);
        getUserById(user2Id);

        userStorage.addFriend(user1Id, user2Id);
        return user1;
    }

    public User deleteUserFromFriendList(Long user1Id, Long user2Id) {
        User user1 = getUserById(user1Id);
        getUserById(user2Id);

        userStorage.removeFriend(user1Id, user2Id);
        return user1;
    }

    public List<User> getFriendList(Long userId) {
        getUserById(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (userStorage.getUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("User with id " + id + "not found"));
    }

    public List<User> showCommonFriendsList(Long userId1, Long userId2) {
        Set<Long> friends1 = userStorage.getFriends(userId1).stream().map(User::getId).collect(Collectors.toSet());
        Set<Long> friends2 = userStorage.getFriends(userId2).stream().map(User::getId).collect(Collectors.toSet());
        friends1.retainAll(friends2);

        List<User> commonFriends = new ArrayList<>();
        for (Long friendId : friends1) {
            userStorage.getUserById(friendId).ifPresent(commonFriends::add);
        }
        return commonFriends;
    }

    public List<Film> getRecommendationsFilms(Long userId) {
        getUserById(userId);
        return userStorage.getRecommendationsFilms(userId);
    }
}