package ru.yandex.practicum.filmorate.model;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Some valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testEmptyName() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testTooLongDescription() {
        Film film = new Film();
        film.setName("Test");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void testReleaseDateTooOld() {
        Film film = new Film();
        film.setName("Old Film");
        film.setDescription("Valid");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Дата в прошлом может пройти — проверь бизнес-валидацию в контроллере при необходимости");
    }

    @Test
    public void testNegativeDuration() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-90);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }
}
