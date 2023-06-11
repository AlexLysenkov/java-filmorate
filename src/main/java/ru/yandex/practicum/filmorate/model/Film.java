package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;


@Data
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;
    private final List<Integer> likes;
    private final LinkedHashSet<Genre> genres;
    @NotNull
    private Mpa mpa;
}
