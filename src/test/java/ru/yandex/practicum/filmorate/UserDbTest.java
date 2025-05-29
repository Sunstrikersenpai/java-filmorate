package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FilmRowMapper.class})
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql")
@Sql(scripts = "classpath:test.sql")
class UserDbTest {

    private final UserDbStorage userDbStorage;

    @Test
    @DirtiesContext
    void testAddUser() {
        User user = new User();
        user.setName("Test User");
        user.setLogin("testlogin");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User savedUser = userDbStorage.addUser(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DirtiesContext
    void testGetUserById() {
        Optional<User> user = userDbStorage.getUserById(1L);

        assertThat(user).isPresent();
        assertThat(user.get().getName()).isEqualTo("ss ss");
    }

    @Test
    @DirtiesContext
    void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Updated Name");
        user.setLogin("ss");
        user.setEmail("updated@example.com");
        user.setBirthday(LocalDate.of(1997, 1, 1));

        userDbStorage.updateUser(user);
        Optional<User> updatedUser = userDbStorage.getUserById(1L);

        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.get().getEmail()).isEqualTo("updated@example.com");
    }
}
