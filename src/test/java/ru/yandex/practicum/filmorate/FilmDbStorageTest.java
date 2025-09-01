package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    public void testCreateFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Set<Genre> genres = new HashSet<>();
        Genre genre = new Genre();
        genre.setId(1L);
        genres.add(genre);

        Film film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film savedFilm = filmStorage.add(film);

        assertThat(savedFilm.getId()).isPositive();
        assertThat(savedFilm.getName()).isEqualTo("Test Film");
        assertThat(savedFilm.getDescription()).isEqualTo("Test Description");
    }

    @Test
    public void testFindFilmById() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Film film = Film.builder()
                .name("Find Film")
                .description("Find Description")
                .releaseDate(LocalDate.of(2005, 5, 15))
                .duration(90)
                .mpa(mpa)
                .build();

        Film savedFilm = filmStorage.add(film);

        Optional<Film> filmOptional = filmStorage.getById(savedFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(filmFound ->
                        assertThat(filmFound).hasFieldOrPropertyWithValue("id", savedFilm.getId())
                );
    }

    @Test
    public void testUpdateFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Film film = Film.builder()
                .name("Update Film")
                .description("Update Description")
                .releaseDate(LocalDate.of(2010, 10, 10))
                .duration(150)
                .mpa(mpa)
                .build();

        Film savedFilm = filmStorage.add(film);

        savedFilm.setName("Updated Film");
        savedFilm.setDescription("Updated Description");

        Film updatedFilm = filmStorage.update(savedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void testGetAllFilms() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Film film1 = Film.builder()
                .name("Film 1")
                .description("Description 1")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(mpa)
                .build();

        Film film2 = Film.builder()
                .name("Film 2")
                .description("Description 2")
                .releaseDate(LocalDate.of(2005, 5, 15))
                .duration(90)
                .mpa(mpa)
                .build();

        filmStorage.add(film1);
        filmStorage.add(film2);

        List<Film> allFilms = filmStorage.getAll();

        assertThat(allFilms.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testAddAndRemoveLike() {
        Mpa mpa = new Mpa();
        mpa.setId(1L);

        Film film = Film.builder()
                .name("Like Film")
                .description("Like Description")
                .releaseDate(LocalDate.of(2015, 3, 20))
                .duration(180)
                .mpa(mpa)
                .build();

        Film savedFilm = filmStorage.add(film);

        // Добавляем лайк от пользователя с ID 1 (который должен существовать в data.sql)
        Long userId = 1L;
        filmStorage.addLike(savedFilm.getId(), userId);

        List<Long> likes = filmStorage.getLikes(savedFilm.getId());
        assertThat(likes).asList().contains(userId);

        // Удаляем лайк
        filmStorage.removeLike(savedFilm.getId(), userId);

        likes = filmStorage.getLikes(savedFilm.getId());
        assertThat(likes).asList().doesNotContain(userId);
    }
}
