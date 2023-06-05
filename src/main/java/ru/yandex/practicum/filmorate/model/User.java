package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    @NotBlank
    @Email
    private final String email;
    @NotNull
    @NotBlank
    private final String login;
    private String name;
    @NotNull
    @Past(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();
}
