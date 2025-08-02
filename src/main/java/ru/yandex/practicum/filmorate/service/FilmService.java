package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId)
                .orElseThrow(() -> new ValidationException("Фильм не найден"));
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getById(filmId)
                .orElseThrow(() -> new ValidationException("Фильм не найден"));
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
