package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(int userId);

    Map<Integer, User> getUsers();

    User deleteUserById (int userId);
}