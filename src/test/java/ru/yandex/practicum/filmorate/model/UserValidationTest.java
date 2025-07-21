package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private Validator validator;

    private static final LocalDate FUTURE_DATE = LocalDate.now().plusYears(1);

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private User createValidUser() {
        return User.builder()
                .email("valid@example.com")
                .login("validLogin")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    @DisplayName("Должен создать валидного пользователя")
    void shouldCreateValidUser() {
        User user = createValidUser();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Валидный пользователь не должен иметь нарушений");
    }

    @Test
    @DisplayName("Не должен пропускать пустой email")
    void shouldFailWhenEmailIsBlank() {
        User user = createValidUser();
        user.setEmail(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Пустой email должен вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Email не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Не должен пропускать некорректный email")
    void shouldFailWhenEmailIsInvalid() {
        User user = createValidUser();
        user.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Некорректный email должен вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Email должен быть валидным", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Не должен пропускать пустой логин")
    void shouldFailWhenLoginIsBlank() {
        User user = createValidUser();
        user.setLogin(" ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Пустой логин должен вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Логин не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Не должен пропускать логин с пробелами")
    void shouldFailWhenLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("login with spaces");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Логин с пробелами должен вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен подставлять логин как имя, если имя не указано")
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        User user = createValidUser();
        user.setName(null);

        assertEquals(user.getLogin(), user.getName(), "Имя должно быть равно логину, если имя не указано");
    }

    @Test
    @DisplayName("Не должен пропускать дату рождения в будущем")
    void shouldFailWhenBirthdayIsInFuture() {
        User user = createValidUser();
        user.setBirthday(FUTURE_DATE);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Дата рождения в будущем должна вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен принимать пустое имя")
    void shouldAcceptEmptyName() {
        User user = createValidUser();
        user.setName("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Пустое имя должно быть допустимым");
    }
}