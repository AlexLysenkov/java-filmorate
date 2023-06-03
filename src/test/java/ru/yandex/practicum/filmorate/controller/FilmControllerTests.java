package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FilmControllerTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Autowired
    private FilmController filmController;
    Film film;

    @Test
    void dateBefore1895() {
        film = new Film("test", "test", LocalDate.of(1860,5,25), 45);
        assertThrows(ValidationException.class, () -> filmController.validateFilm(film));
    }

    @Test
    void emptyNameTest() {
        film = new Film("", "test", LocalDate.of(1860,5,25), 45);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    void emptyDescriptionTest() {
        film = new Film("test", "", LocalDate.of(1860, 5,25), 45);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    void symbolsLengthDescriptionTest() {
        film = new Film("test", ("a").repeat(201), LocalDate.of(1860,5,25),
                45);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    void positiveDurationTest() {
        film = new Film("test", "test", LocalDate.of(1860,5,25), -30);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }
}
