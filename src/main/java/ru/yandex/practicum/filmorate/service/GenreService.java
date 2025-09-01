package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GenreService {

    private final GenreStorage genreStorage;
    private final GenreDbStorage genreDbStorage;

    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage, GenreDbStorage genreDbStorage) {
        this.genreStorage = genreStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public List<Genre> getAllGenres() {
        return genreStorage.getAll();
    }

    public Genre getGenreById(Long id) {
        return genreStorage.getById(id)
                .orElseThrow(() -> new NoSuchElementException("Жанр с id " + id + " не найден."));
    }
}
