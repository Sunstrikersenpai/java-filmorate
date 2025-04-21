package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage users;

    @Autowired
    public UserService(UserStorage users) {
        this.users = users;
    }

    public User addUserToFriendList(Long user1Id, Long user2Id) {
        User user1 = users.getMapUsers().get(user1Id);
        User user2 = users.getMapUsers().get(user2Id);
        if (user1 == null || user2 == null) {
            throw new NotFoundException("User not found");
        }
        user1.addToFriendList(user2Id);
        user2.addToFriendList(user1Id);
        return user1;
    }

    public User deleteUserFromFriendList(Long user1Id, Long user2Id) {
        User user1 = users.getMapUsers().get(user1Id);
        User user2 = users.getMapUsers().get(user2Id);
        if (user1 == null || user2 == null) {
            throw new NotFoundException("User not found");
        }
        user1.getFriendList().remove(user2Id);
        user2.getFriendList().remove(user1Id);
        return user1;
    }

    public List<User> getFriendList(Long userId) {
        Map<Long, User> userMap = users.getMapUsers();
        Set<Long> friendIds = userMap.get(userId).getFriendList();

        return friendIds.stream()
                .map(userMap::get)
                .toList();
    }

    public List<User> showCommonFriendsList(Long userId1, Long userId2) {
        Map<Long, User> userMap = users.getMapUsers();
        User user1 = userMap.get(userId1);
        User user2 = userMap.get(userId2);

        if (user1 == null) {
            throw new NotFoundException("User id "+ userId1 + "not found");
        }
        if (user2 == null) {
            throw new NotFoundException("User id "+ userId2 + "not found");
        }

        Set<Long> commonFriendIds = new HashSet<>(user1.getFriendList());
        commonFriendIds.retainAll(user2.getFriendList());

        return commonFriendIds.stream()
                .map(userMap::get)
                .toList();
    }
}
