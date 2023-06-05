package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Фильм сохранен: {}", film.getName());
        return filmService.createFilm(film);
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return filmService.getAllFilms();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Информация о фильме обновлена, id: {}, название: {}", film.getId(), film.getName());
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        log.info(String.format("Получение фильма с id = %d", id));
        return filmService.getFilmById(id);
    }

    @DeleteMapping("/{id}")
    public Film deleteFilmById(@PathVariable int id) {
        log.info(String.format("Удаление фильма с id = %d", id));
        return filmService.deleteFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void getLike(@PathVariable int id, @PathVariable int userId) {
        log.info(String.format("Пользователю с id: %d понравился фильм с id: %d", userId, id));
        filmService.getLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info(String.format("Пользователь с id: %d удалил лайк с фильма с id: %d", userId, id));
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Получение списка популярных фильмов");
        return filmService.getPopularFilms(count);
    }
}