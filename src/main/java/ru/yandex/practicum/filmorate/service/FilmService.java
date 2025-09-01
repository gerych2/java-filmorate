package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.*;
import java.util.stream.Collectors;
import java.util.NoSuchElementException;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       FilmDbStorage filmDbStorage,
                       @Qualifier("mpaDbStorage") MpaStorage mpaStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmDbStorage = filmDbStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Film addFilm(Film film) {
        // Проверяем существование MPA
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            mpaStorage.getById(film.getMpa().getId())
                    .orElseThrow(() -> new NoSuchElementException("MPA с id " + film.getMpa().getId() + " не найден."));
        }

        // Проверяем существование жанров
        if (film.getGenres() != null) {
            for (var genre : film.getGenres()) {
                if (genre.getId() != null) {
                    genreStorage.getById(genre.getId())
                            .orElseThrow(() -> new NoSuchElementException("Жанр с id " + genre.getId() + " не найден."));
                }
            }
        }

        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        // Проверяем существование фильма перед обновлением
        getFilmById(film.getId());
        return filmStorage.update(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Фильм с id " + id + " не найден."));
    }

    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId); // Проверяем существование фильма
        checkUserExists(userId);
        filmDbStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        getFilmById(filmId); // Проверяем существование фильма
        checkUserExists(userId);
        filmDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.getAll();
        return films.stream()
                .sorted(Comparator.comparingInt((Film f) -> filmDbStorage.getLikes(f.getId()).size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkUserExists(Long userId) {
        userStorage.getById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь с id " + userId + " не найден."));
    }
}
