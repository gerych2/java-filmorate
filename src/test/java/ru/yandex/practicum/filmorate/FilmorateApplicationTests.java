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
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Test
	void contextLoads() {
		// Стандартный тест Spring Boot
	}

	// Film validation tests
	@Test
	void whenFilmNameIsBlank_thenValidationFails() {
		Film film = Film.builder()
				.name("   ")
				.description("Valid description")
				.releaseDate(LocalDate.of(2000, 1, 1))
				.duration(120)
				.build();

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Should fail because name is blank");
		assertTrue(violations.stream()
						.anyMatch(v -> v.getMessage().equals("Название не может быть пустым")),
				"Should have correct validation message");
	}

	@Test
	void whenFilmDescriptionTooLong_thenValidationFails() {
		String longDescription = "a".repeat(201);
		Film film = Film.builder()
				.name("Valid Name")
				.description(longDescription)
				.releaseDate(LocalDate.of(2000, 1, 1))
				.duration(120)
				.build();

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Should fail because description is too long");
		assertTrue(violations.stream()
						.anyMatch(v -> v.getMessage().equals("Описание не может превышать 200 символов")),
				"Should have correct validation message");
	}

	@Test
	void whenFilmDurationNegative_thenValidationFails() {
		Film film = Film.builder()
				.name("Valid Name")
				.description("Valid description")
				.releaseDate(LocalDate.of(2000, 1, 1))
				.duration(-5)
				.build();

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Should fail because duration is negative");
		assertTrue(violations.stream()
						.anyMatch(v -> v.getMessage().equals("Продолжительность должна быть положительной")),
				"Should have correct validation message");
	}

	// User validation tests
	@Test
	void whenUserEmailInvalid_thenValidationFails() {
		User user = User.builder()
				.email("invalid-email")
				.login("validlogin")
				.birthday(LocalDate.of(1990, 1, 1))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Should fail because email is invalid");
		assertTrue(violations.stream()
						.anyMatch(v -> v.getMessage().equals("Email должен быть валидным")),
				"Should have correct validation message");
	}

	@Test
	void whenUserLoginContainsSpaces_thenValidationFails() {
		User user = User.builder()
				.email("valid@example.com")
				.login("user login")
				.birthday(LocalDate.of(1990, 1, 1))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Should fail because login contains spaces");
		assertTrue(violations.stream()
						.anyMatch(v -> v.getMessage().equals("Логин не может содержать пробелы")),
				"Should have correct validation message");
	}

	@Test
	void whenUserBirthdayInFuture_thenValidationFails() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.birthday(LocalDate.now().plusDays(1))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Should fail because birthday is in future");
		assertTrue(violations.stream()
						.anyMatch(v -> v.getMessage().equals("Дата рождения не может быть в будущем")),
				"Should have correct validation message");
	}

	@Test
	void whenValidFilm_thenNoValidationErrors() {
		Film film = Film.builder()
				.name("Valid Film")
				.description("Valid description")
				.releaseDate(LocalDate.of(2000, 1, 1))
				.duration(120)
				.build();

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertTrue(violations.isEmpty(), "Valid film should pass validation");
	}

	@Test
	void whenValidUser_thenNoValidationErrors() {
		User user = User.builder()
				.email("valid@example.com")
				.login("validlogin")
				.birthday(LocalDate.of(1990, 1, 1))
				.build();

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.isEmpty(), "Valid user should pass validation");
	}
}