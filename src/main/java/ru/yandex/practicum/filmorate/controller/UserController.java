package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Пользователь сохранен, логин: {}, email: {}", user.getLogin(), user.getEmail());
        return user;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        validateUser(user);
        if(!users.containsKey(user.getId())) {
            throw new ValidationException("Такого пользователя не существует");
        }
        log.info("Информация о пользователе обновлена, id: {}, логин: {}", user.getId(), user.getLogin());
        users.put(user.getId(), user);
        return user;
    }


    public void validateUser(@RequestBody User user) {
        if(user.getLogin().contains(" ")) {
            log.warn("Логин пользователя: {}", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Введенная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
