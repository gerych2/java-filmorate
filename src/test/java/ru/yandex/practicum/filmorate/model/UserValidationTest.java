package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        User user = User.builder()
                .email("valid@example.com")
                .login("validLogin")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertEquals("validLogin", user.getName(),
                "Имя должно быть равно логину, если имя не указано");
    }

    @Test
    void shouldKeepNameWhenNameIsProvided() {
        User user = User.builder()
                .email("valid@example.com")
                .login("validLogin")
                .name("Custom Name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        assertEquals("Custom Name", user.getName(),
                "Имя должно сохраняться, если оно указано");
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        User user = User.builder()
                .email(" ")
                .login("validLogin")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для пустого email");
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        User user = User.builder()
                .email("valid@example.com")
                .login(" ")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для пустого логина");
    }
}