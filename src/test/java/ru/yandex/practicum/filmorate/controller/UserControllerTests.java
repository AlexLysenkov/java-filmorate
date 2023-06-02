package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private UserController userController;
    User user;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    void userEmptyNameTest() {
        user = new User("test@yandex.ru", "login", LocalDate.of(1965, 11, 23));
        userController.validateUser(user);
        assertEquals("login", user.getName());
        User user1 = new User("test@yandex.ru", "login", LocalDate.of(1965, 11, 23));
        user1.setName(" ");
        userController.validateUser(user1);
        assertEquals("login", user1.getName());
    }

    @Test
    void emailEmptyTest() {
        user = new User("", "login", LocalDate.of(1965, 11, 23));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void emailWithoutAtSymbolTest() {
        user = new User("test.yandex.ru", "login", LocalDate.of(1965, 11, 23));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void loginBlankTest() {
        user = new User("test@yandex.ru", " ", LocalDate.of(1965, 11, 23));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void loginEmptyTest() {
        user = new User("test@yandex.ru", "", LocalDate.of(1965, 11, 23));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    void birthdayFutureTest() {
        user = new User("test@yandex.ru", "login", LocalDate.of(2077, 11, 23));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }
}
