package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.utils.Constants.USER_NOT_FOUND;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId).orElseThrow(() -> {
            log.warn(String.format(USER_NOT_FOUND, userId));
            return new ObjectNotFoundException(String.format(USER_NOT_FOUND, userId));
        });
    }

    public User deleteUserById(int userId) {
        return userStorage.deleteUserById(userId);
    }

    public void addFriends(int firstId, int secondId) {
        if (getUserById(firstId).getFriends().contains(secondId)) {
            acceptFriendRequest(firstId, secondId);
        } else {
            sendFriendRequest(firstId, secondId);
        }
    }

    public void sendFriendRequest(int firstId, int secondId) {
        userStorage.sendFriendRequest(firstId, secondId);
    }

    public void acceptFriendRequest(int firsId, int secondId) {
        userStorage.acceptFriendRequest(firsId, secondId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getUserFriends(int id) {
        var user = getUserById(id);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int firstId, int secondId) {
        return getUserFriends(firstId).stream()
                .filter(getUserFriends(secondId)::contains)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Логин пользователя: {}", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пользователя: '{}' - заменено на логин", user.getName());
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Введенная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Email пользователя: {}", user.getEmail());
            throw new ValidationException("Некорректный email");
        }
    }
}
