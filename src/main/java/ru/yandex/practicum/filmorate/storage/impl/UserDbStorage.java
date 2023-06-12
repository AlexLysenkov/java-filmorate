package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users", (resultSet, rowNum) -> makeUser(resultSet));
    }

    @Override
    public User createUser(User user) {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        SqlRowSet lastIdRow = jdbcTemplate.queryForRowSet("SELECT MAX(user_id) last_id FROM users");
        if (lastIdRow.next()) {
            user.setId(lastIdRow.getInt("last_id"));
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        SqlRowSet usersRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", user.getId());
        if (!usersRows.next()) {
            setUserNotFound(user.getId());
        }
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserById(int userId) {
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(
                "SELECT * FROM users WHERE user_id = ?", userId);
        if (userRow.next()) {
            User user = User.builder()
                    .birthday(Objects.requireNonNull(userRow.getDate("birthday")).toLocalDate())
                    .email(Objects.requireNonNull(userRow.getString("email")))
                    .login(Objects.requireNonNull(userRow.getString("login")))
                    .name(userRow.getString("name"))
                    .id(userRow.getInt("user_id"))
                    .build();
            user.getFriends().addAll(getFriends(userId));
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public User deleteUserById(int userId) {
        Optional<User> user = getUserById(userId);
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", userId);
        return user.orElse(null);
    }

    @Override
    public void sendFriendRequest(int user1Id, int user2Id) {
        if (getUserById(user1Id).isEmpty()) {
            setUserNotFound(user1Id);
        }
        if (getUserById(user2Id).isEmpty()) {
            setUserNotFound(user2Id);
        }
        jdbcTemplate.update("INSERT INTO friends (user1_id, user2_id, status) VALUES (?, ?, ?)",
                user1Id, user2Id, "NOT_APPROVED");
    }

    @Override
    public void acceptFriendRequest(int firstId, int secondId) {
        if (getUserById(firstId).isEmpty()) {
            setUserNotFound(firstId);
        }
        if (getUserById(secondId).isEmpty()) {
            setUserNotFound(secondId);
        }
        jdbcTemplate.update("UPDATE friends SET status = ? WHERE user1_id = ? AND user2_id = ?",
                "FRIEND", secondId, firstId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet("SELECT * FROM friends " +
                        "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)",
                userId, friendId, friendId, userId);
        if (friendRows.next()) {
            int user1Id = friendRows.getInt("user1_id");
            int user2Id = friendRows.getInt("user2_id");
            if (user1Id == userId) {
                jdbcTemplate.update("DELETE FROM friends WHERE (user1_id = ? AND user2_id = ?)",
                        userId, friendId);
            } else {
                jdbcTemplate.update("UPDATE friends SET status = 'DECLINED' WHERE user1_id = ? AND user2_id = ?",
                        user2Id, user1Id);
            }
        }
    }

    private List<Integer> getFriends(int userId) {
        return jdbcTemplate.query("SELECT user2_id FROM friends WHERE user1_id = ?",
                (resultSet, rowNum) -> resultSet.getInt("user2_id"), userId);
    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("user_id");
        User user = User.builder()
                .id(id)
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .name(resultSet.getString("name"))
                .build();
        user.getFriends().addAll(getFriends(id));
        return user;
    }

    private void setUserNotFound(int userId) {
        log.warn(String.format("Пользователя с таким id = %d не существует", userId));
        throw new ObjectNotFoundException(String.format("Пользователя с таким id = %d не существует", userId));
    }
}
