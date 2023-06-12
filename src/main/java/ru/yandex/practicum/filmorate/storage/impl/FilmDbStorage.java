package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.utils.Constants.FILM_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query("SELECT * FROM films", (resultSet, rowNum) -> makeFilm(resultSet));
    }

    @Override
    public Film createFilm(Film film) {
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                        "VALUES (?, ?, ?, ?, ?)", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        SqlRowSet lastIdRow = jdbcTemplate.queryForRowSet("SELECT MAX(film_id) last_id FROM films");
        if (lastIdRow.next()) {
            film.setId(lastIdRow.getInt("last_id"));
        }
        if (film.getGenres() != null) {
            addGenre(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        SqlRowSet filmsRow = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE film_id = ?", film.getId());
        if (!filmsRow.next()) {
            log.warn(String.format(FILM_NOT_FOUND, film.getId()));
            throw new ObjectNotFoundException(String.format(FILM_NOT_FOUND, film.getId()));
        }
        jdbcTemplate.update("UPDATE films SET name = ?, description = ?, release_date = ?," +
                        " duration = ?, mpa_id = ? WHERE film_id = ?", film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        updateGenre(film);
        return film;
    }

    private void updateGenre(Film film) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (!Objects.isNull(film.getGenres())) {
            addGenre(film);
        }
    }

    private void addGenre(Film film) {
        jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, new ArrayList<>(film.getGenres()).get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return film.getGenres().size();
                    }
                });
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films WHERE film_id = ?", (resultSet, rowNum) ->
                makeFilm(resultSet), filmId);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(films.get(0));
    }

    @Override
    public Optional<Film> deleteFilmById(int id) {
        Optional<Film> film = getFilmById(id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
        return film;
    }

    @Override
    public void getLike(Film film, int userId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM likes " +
                "WHERE film_id = ? AND user_id = ?", film.getId(), userId);
        if (!sqlRowSet.next()) {
            jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film.getId(), userId);
        }
    }

    @Override
    public void removeLike(Film film, int userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", film.getId(), userId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return jdbcTemplate.query("SELECT f.* FROM films f LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?", (resultSet, rowNum) -> makeFilm(resultSet), count);
    }

    private Genre makeGenre(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("genre_id");
        String name = resultSet.getString("name");
        return new Genre(id, name);
    }

    private Mpa makeMpa(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("mpa_id");
        String name = resultSet.getString("name");
        return new Mpa(id, name);
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        Integer id = resultSet.getInt("film_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        List<Integer> likes = jdbcTemplate.query("SELECT user_id FROM likes WHERE film_id = ?",
                (rowSet, rowNum) -> rowSet.getInt("user_id"),
                id);
        List<Genre> genres = jdbcTemplate.query("SELECT g.genre_id, g.name FROM genres g " +
                "LEFT JOIN film_genre fg ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ?", (rowSet, rowNum) -> makeGenre(rowSet), id);
        List<Mpa> mpa = jdbcTemplate.query("SELECT m.mpa_id mpa_id, m.name name FROM mpa m " +
                "LEFT JOIN films f ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?", (rowSet, rowNum) -> makeMpa(rowSet), id);
        return new Film(id, name, description, releaseDate, duration, likes, new LinkedHashSet<>(genres), mpa.get(0));
    }
}
