package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/datatest.sql"})
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;

    @Test
    public void findUserByIdTest() {
        Optional<User> userOptional = userDbStorage.getUserById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void updateUserTest() {
        Optional<User> userOptional = userDbStorage.getUserById(1);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setName("Who");
            userDbStorage.updateUser(user);
        }
        Optional<User> userOptional1 = userDbStorage.getUserById(1);
        assertThat(userOptional1)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "Who")
                );
    }

    @Test
    public void createUserTest() {
        User user = new User(null, "test@email.ru", "test", "test",
                LocalDate.of(1994, Month.DECEMBER, 14));
        User user1 = userDbStorage.createUser(user);
        Optional<User> userOptional = userDbStorage.getUserById(user1.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("name", "test")
                );
    }

    @Test
    public void deleteUserTest() {
        userDbStorage.deleteUserById(2);
        Optional<User> userOptional = userDbStorage.getUserById(2);
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void sendFriendRequestTest() {
        userDbStorage.sendFriendRequest(4, 6);
        Optional<User> userOptional = userDbStorage.getUserById(4);
        assertTrue(userOptional.get().getFriends().contains(6));
    }

    @Test
    public void acceptRequestFriendTest() {
        userDbStorage.sendFriendRequest(5, 6);
        userDbStorage.acceptFriendRequest(6, 5);
        Optional<User> userOptional = userDbStorage.getUserById(6);
        assertFalse(userOptional.get().getFriends().contains(5));
    }

    @Test
    public void removeFriendTest() {
        userDbStorage.sendFriendRequest(3, 6);
        userDbStorage.removeFriend(3, 6);
        Optional<User> userOptional = userDbStorage.getUserById(3);
        assertFalse(userOptional.get().getFriends().contains(6));
    }
}
