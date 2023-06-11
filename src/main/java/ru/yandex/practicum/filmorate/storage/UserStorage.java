package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUserById(int userId);

    User deleteUserById(int userId);

    void sendFriendRequest(int user1Id, int user2Id);

    void acceptFriendRequest(int user1Id, int user2Id);

    void removeFriend(int userId, int friendId);
}
