package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.utils.Constants.FILM_NOT_FOUND;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId).orElseThrow(() -> {
            log.warn(String.format(FILM_NOT_FOUND, filmId));
            throw new ObjectNotFoundException(String.format(FILM_NOT_FOUND, filmId));
        });
    }

    public Film deleteFilmById(int id) {
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

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Дата выхода фильма: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не должна быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Описание фильма: {}", film.getDescription());
            throw new ValidationException("Максимальная длина описания должна быть 200 символов");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма: {}", film.getName());
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }
}
