package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.utils.Constants.USER_NOT_FOUND;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int userId) {
        checkUser(userId);
        return userStorage.getUserById(userId);
    }

    public User deleteUserById(int userId) {
        checkUser(userId);
        return userStorage.deleteUserById(userId);
    }

    public void addFriends(int firstId, int secondId) {
        checkUser(firstId);
        checkUser(secondId);
        userStorage.getUserById(firstId).getFriends().add(secondId);
        userStorage.getUserById(secondId).getFriends().add(firstId);
    }

    public void removeFriends(int firstId, int secondId) {
        checkUser(firstId);
        checkUser(secondId);
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

    private void checkUser(int id) {
        if (!userStorage.getUsers().containsKey(id)) {
            log.warn(String.format(USER_NOT_FOUND, id));
            throw new ObjectNotFoundException(String.format(USER_NOT_FOUND, id));
        }
    }
}
