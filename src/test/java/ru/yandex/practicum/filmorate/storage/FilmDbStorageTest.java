package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/datatest.sql"})
public class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;

    @Test
    public void getByIdTest() {
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    public void updateFilmTest() {
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();
            film.setName("testName");
            filmDbStorage.updateFilm(film);
        }
        Optional<Film> filmOptional1 = filmDbStorage.getFilmById(1);
        assertThat(filmOptional1)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "testName"));
    }

    @Test
    public void createFilmTest() {
        Film film = new Film(null, "filmTest", "filmTest description",
                LocalDate.of(2009, Month.MAY, 5), 314, Collections.emptyList(),
                null, new Mpa(1, "G"));
        Film film1 = filmDbStorage.createFilm(film);
        Optional<Film> filmOptional = filmDbStorage.getFilmById(film1.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", "filmTest"));
    }

    @Test
    public void deleteFilmTest() {
        filmDbStorage.deleteFilmById(1);
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);
        assertThat(filmOptional).isEmpty();
    }

    @Test
    public void getLikeTest() {
        Optional<Film> filmOptional = filmDbStorage.getFilmById(1);
        filmOptional.ifPresent(film -> filmDbStorage.getLike(film, 4));
        Optional<Film> filmOptional1 = filmDbStorage.getFilmById(1);
        assertTrue(filmOptional1.get().getLikes().contains(4));
    }

    @Test
    public void removeLikeTest() {
        Optional<Film> filmOptional = filmDbStorage.getFilmById(2);
        filmOptional.ifPresent(film -> filmDbStorage.getLike(film, 5));
        filmOptional.ifPresent(film -> filmDbStorage.removeLike(film, 5));
        Optional<Film> filmOptional1 = filmDbStorage.getFilmById(2);
        assertFalse(filmOptional1.get().getLikes().contains(5));
    }
}
