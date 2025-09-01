package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class})
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreStorage.getAll();
        
        assertThat(genres).isNotNull();
        assertThat(genres.size()).isGreaterThan(0);
        
        // Проверяем, что жанры отсортированы по id
        for (int i = 1; i < genres.size(); i++) {
            assertThat(genres.get(i).getId()).isGreaterThan(genres.get(i-1).getId());
        }
    }

    @Test
    public void testGetGenreById() {
        // Получаем первый жанр из списка
        List<Genre> allGenres = genreStorage.getAll();
        assertThat(allGenres.size()).isGreaterThan(0);
        
        Genre firstGenre = allGenres.get(0);
        Optional<Genre> foundGenre = genreStorage.getById(firstGenre.getId());
        
        assertThat(foundGenre)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre.getId()).isEqualTo(firstGenre.getId())
                );
    }

    @Test
    public void testGetGenreByIdNotFound() {
        Optional<Genre> genre = genreStorage.getById(999L);
        
        assertThat(genre).isEmpty();
    }
}
