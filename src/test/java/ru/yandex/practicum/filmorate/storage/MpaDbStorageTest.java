package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/datatest.sql"})
public class MpaDbStorageTest {
    private final MpaStorage mpaStorage;

    @Test
    public void getAllMpaTest() {
        ArrayList<Mpa> mpa = (ArrayList<Mpa>) mpaStorage.getAllMpa();
        assertEquals(5, mpa.size());
        assertEquals(mpa.get(0).getName(), "G");
    }

    @Test
    public void getMpaByIdTest() {
        Optional<Mpa> mpaOptional = mpaStorage.getMpaById(3);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("name", "PG-13"));
    }
}
