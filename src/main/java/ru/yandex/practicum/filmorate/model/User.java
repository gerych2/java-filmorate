package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {

    private int id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть валидным")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @Builder.Default
    private Set<Integer> friends = new HashSet<>();

    // Кастомный билдер
    @Builder
    public User(int id,
                String email,
                String login,
                String name,
                LocalDate birthday,
                Set<Integer> friends) {

        this.id = id;
        this.email = email;
        this.login = login;
        // Если имя пустое, используем логин
        this.name = (name == null || name.isBlank()) ? login : name;
        this.birthday = birthday;
        this.friends = (friends == null) ? new HashSet<>() : friends;
    }
}
