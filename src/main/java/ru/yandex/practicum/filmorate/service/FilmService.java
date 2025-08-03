package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private void validateReleaseDate(Film film) {
        LocalDate cinemaBirth = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(cinemaBirth)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    public Film addFilm(Film film) {
        validateReleaseDate(film);
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        validateReleaseDate(film);
        return filmStorage.update(film);
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Фильм не найден"));
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Фильм не найден"));
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
