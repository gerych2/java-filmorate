package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не может быть пустой");
        }
        return filmStorage.add(film);
    }


    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public void addLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        checkUserExists(userId);
        film.getLikes().add(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = getFilmById(filmId);
        checkUserExists(userId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmById(int id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм с id " + id + " не найден."));
    }

    private void checkUserExists(int userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id " + userId + " не найден."));
    }
}
