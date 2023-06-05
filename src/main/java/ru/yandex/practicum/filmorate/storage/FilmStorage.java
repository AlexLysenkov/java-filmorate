package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int filmId);

    Map<Integer, Film> getFilms();

    Film deleteFilmById(int id);

    void getLike(Film film, int userId);

    void removeLike(Film film, int userId);

    Collection<Film> getPopularFilms(int count);
}