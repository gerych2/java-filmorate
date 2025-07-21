package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	private static Validator validator;

	@BeforeAll
	static void setupValidator() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void contextLoads() {
		// Стандартный тест Spring Boot
	}

	// Тест валидации для Film
	@Test
	void whenFilmNameIsBlank_thenValidationFails() {
		Film film = new Film();
		film.setName("  "); // пустое имя
		film.setDescription("Описание");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Имя фильма не может быть пустым");
	}

	@Test
	void whenFilmDurationNegative_thenValidationFails() {
		Film film = new Film();
		film.setName("Valid Name");
		film.setDescription("Описание");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(-5);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Продолжительность должна быть положительной");
	}

	// Тест валидации для User
	@Test
	void whenUserEmailInvalid_thenValidationFails() {
		User user = new User();
		user.setEmail("invalid-email");
		user.setLogin("userlogin");
		user.setBirthday(LocalDate.of(1990, 1, 1));

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Email должен быть валидным");
	}

	@Test
	void whenUserLoginContainsSpaces_thenValidationFails() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("user login");
		user.setBirthday(LocalDate.of(1990, 1, 1));

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Логин не должен содержать пробелы");
	}
}
