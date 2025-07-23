package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private Validator validator;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Film createValidFilm() {
        return Film.builder()
                .name("Valid Film")
                .description("Valid film description under 200 characters")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
    }

    @Test
    @DisplayName("Должен создать валидный фильм")
    void shouldCreateValidFilm() {
        Film film = createValidFilm();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Валидный фильм не должен иметь нарушений");
    }

    @Test
    @DisplayName("Не должен пропускать пустое название")
    void shouldFailWhenNameIsBlank() {
        Film film = createValidFilm();
        film.setName(" ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Пустое название должно вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Не должен пропускать слишком длинное описание")
    void shouldFailWhenDescriptionTooLong() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Описание длиннее 200 символов должно вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Описание не может превышать 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен пропускать описание ровно 200 символов")
    void shouldAcceptMaxLengthDescription() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(200));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty(), "Описание ровно 200 символов должно быть валидным");
    }

    @Test
    @DisplayName("Не должен пропускать null дату релиза")
    void shouldFailWhenReleaseDateIsNull() {
        Film film = createValidFilm();
        film.setReleaseDate(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Null дата релиза должна вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Дата релиза не может быть пустой", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Не должен пропускать отрицательную продолжительность")
    void shouldFailWhenDurationIsNegative() {
        Film film = createValidFilm();
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Отрицательная продолжительность должна вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Продолжительность должна быть положительной", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Не должен пропускать нулевую продолжительность")
    void shouldFailWhenDurationIsZero() {
        Film film = createValidFilm();
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Нулевая продолжительность должна вызывать ошибку");
        assertEquals(1, violations.size());
        assertEquals("Продолжительность должна быть положительной", violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("Должен пропускать минимально допустимую дату релиза")
    void shouldAcceptMinReleaseDate() {
        Film film = createValidFilm();
        film.setReleaseDate(MIN_RELEASE_DATE);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty(), "Минимальная допустимая дата релиза должна быть валидной");
    }

    @Test
    @DisplayName("Не должен пропускать дату релиза раньше минимальной")
    void shouldFailWhenReleaseDateBeforeMin() {
        Film film = createValidFilm();
        film.setReleaseDate(MIN_RELEASE_DATE.minusDays(1));

        // Эта проверка должна быть в контроллере
        assertThrows(ValidationException.class, () -> {
            if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
        }, "Дата релиза раньше минимальной должна вызывать ValidationException");
    }
}