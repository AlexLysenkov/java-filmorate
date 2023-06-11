package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/datatest.sql"})
public class GenreDbStorageTest {
    private final GenreStorage genreStorage;

    @Test
    public void getAllGenreTest() {
        ArrayList<Genre> genres = (ArrayList<Genre>) genreStorage.getAllGenres();
        assertEquals(11, genres.size());
        assertEquals(genres.get(0).getName(), "Комедия");
    }

    @Test
    public void getGenreByIdTest() {
        Optional<Genre> genreOptional = genreStorage.getGenreById(1);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия"));
    }
}
