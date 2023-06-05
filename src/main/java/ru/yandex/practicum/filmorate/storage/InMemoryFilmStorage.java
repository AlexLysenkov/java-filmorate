package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 1;
    private final LocalDate releaseBefore = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        film.setId(id++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.warn("Id фильма: {}", film.getId());
            throw new ObjectNotFoundException("Такого фильма не существует");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film deleteFilmById(int id) {
        return films.remove(id);
    }

    @Override
    public void getLike(Film film, int userId) {
        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(Film film, int userId) {
        film.getLikes().remove(userId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(releaseBefore)) {
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
