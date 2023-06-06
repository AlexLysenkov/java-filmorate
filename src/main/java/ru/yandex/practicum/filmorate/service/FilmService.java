package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.utils.Constants.FILM_NOT_FOUND;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        checkFilm(filmId);
        return filmStorage.getFilmById(filmId);
    }

    public Film deleteFilmById(int id) {
        checkFilm(id);
        return filmStorage.deleteFilmById(id);
    }

    public void getLike(int id, int userId) {
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);
        filmStorage.getLike(film, user.getId());
    }

    public void removeLike(int id, int userId) {
        Film film = getFilmById(id);
        User user = userService.getUserById(userId);
        filmStorage.removeLike(film, user.getId());
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void checkFilm(int id) {
        if (!filmStorage.getFilms().containsKey(id)) {
            log.warn(String.format(FILM_NOT_FOUND, id));
            throw new ObjectNotFoundException(String.format(FILM_NOT_FOUND, id));
        }
    }
}
