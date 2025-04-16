package ru.yandex.practicum.filmorate.controllerTests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController controller = new UserController();
    User user;
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @BeforeEach
    void setUp() {
        user = User.builder().name("Nick Name")
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

    }

    @Test
    void addUser_successfully() {
        controller.addUser(user);

        assertTrue(controller.getUsers().contains(user));
        assertEquals(1, user.getId());
    }

    @Test
    void uptUser_successfully() {
        controller.addUser(user);
        User uptUser = user.toBuilder().name("NewName").build();
        controller.uptUser(uptUser);

        assertEquals(1, controller.getUsers().size());
        assertNotEquals(controller.getUsers().getFirst(), user);
        assertEquals(controller.getUsers().getFirst(), uptUser);
    }

    @Test
    void addUser_incorrectEmailThrow() {
        User badDataUser = user.toBuilder().email(" ").build();
        Set<ConstraintViolation<User>> violations = validator.validate(badDataUser);

        assertFalse(violations.isEmpty());
    }

    @Test
    void addUser_incorrectLoginThrow() {
        User badDataUser = user.toBuilder().login(" ").build();
        Set<ConstraintViolation<User>> violations = validator.validate(badDataUser);

        assertFalse(violations.isEmpty());
    }

    @Test
    void uptUser_userNotFoundTrows() {
        controller.addUser(user);
        User badDataUser = user.toBuilder().id(999).build();

        assertEquals(1, controller.getUsers().size());
        assertThrows(IllegalArgumentException.class, () -> controller.uptUser(badDataUser));
    }

    @Test
    void getUsers_returnUsersList() {
        User anotherUser = user.toBuilder().build();
        anotherUser.setName("John");

        controller.addUser(anotherUser);
        controller.addUser(user);

        assertTrue(controller.getUsers().contains(user));
        assertTrue(controller.getUsers().contains(anotherUser));
        assertEquals(2, controller.getUsers().size());
    }
}
