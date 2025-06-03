package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;
    private final FilmRowMapper filmRowMapper;

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (name, login, email, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        if (rows == 0) {
            throw new RuntimeException("User not inserted");
        }

        Long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, userId);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.user_id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public void removeUserById(Long userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}