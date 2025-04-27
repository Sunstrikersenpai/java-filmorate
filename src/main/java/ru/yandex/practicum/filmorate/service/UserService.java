package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User addUserToFriendList(Long user1Id, Long user2Id) {
        User user1 = getUserById(user1Id);
        User user2 = getUserById(user2Id);

        user1.addToFriendList(user2Id);
        user2.addToFriendList(user1Id);
        return user1;
    }

    public User deleteUserFromFriendList(Long user1Id, Long user2Id) {
        User user1 = getUserById(user1Id);
        User user2 = getUserById(user2Id);

        user1.getFriendList().remove(user2Id);
        user2.getFriendList().remove(user1Id);
        return user1;
    }

    public List<User> getFriendList(Long userId) {
        Set<Long> friendIds = getUserById(userId).getFriendList();
        Map<Long, User> userMap = userStorage.getMapUsers();

        return friendIds.stream()
                .map(userMap::get)
                .toList();
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("User with id " + id + "not found"));
    }

    public List<User> showCommonFriendsList(Long userId1, Long userId2) {
        Map<Long, User> userMap = userStorage.getMapUsers();
        User user1 = getUserById(userId1);
        User user2 = getUserById(userId2);

        Set<Long> commonFriendIds = new HashSet<>(user1.getFriendList());
        commonFriendIds.retainAll(user2.getFriendList());

        return commonFriendIds.stream()
                .map(userMap::get)
                .toList();
    }
}
