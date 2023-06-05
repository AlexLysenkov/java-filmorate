package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            log.warn(String.format("Фильм с id = %d не существует", userId));
            throw new ObjectNotFoundException(String.format("Фильм с id = %d не существует", userId));
        }
        return userStorage.getUserById(userId);
    }

    public User deleteUserById(int userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            log.warn(String.format("Фильм с id = %d не существует", userId));
            throw new ObjectNotFoundException(String.format("Фильм с id = %d не существует", userId));
        }
        return userStorage.deleteUserById(userId);
    }

    public void addFriends(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId)) {
            log.warn("Id пользователя: {}", firstId);
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        if (!userStorage.getUsers().containsKey(secondId)) {
            log.warn("Id пользователя: {}", secondId);
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        userStorage.getUserById(firstId).getFriends().add(secondId);
        userStorage.getUserById(secondId).getFriends().add(firstId);
    }

    public void removeFriends(int firstId, int secondId) {
        if (!userStorage.getUsers().containsKey(firstId)) {
            log.warn("Id пользователя: {}", firstId);
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        if (!userStorage.getUsers().containsKey(secondId)) {
            log.warn("Id пользователя: {}", secondId);
            throw new ObjectNotFoundException("Такого пользователя не существует");
        }
        userStorage.getUserById(firstId).getFriends().remove(secondId);
        userStorage.getUserById(secondId).getFriends().remove(firstId);
    }

    public List<User> getUserFriends(int id) {
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int firstId, int secondId) {
        User user1 = getUserById(firstId);
        User user2 = getUserById(secondId);
        return user1.getFriends().stream()
                .filter(id -> user2.getFriends().contains(id))
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
