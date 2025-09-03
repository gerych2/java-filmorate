package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Film> filmRowMapper = (ResultSet rs, int rowNum) -> {
        Film film = new Film();
        film.setId(rs.getLong("id")); // поле id
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // MPA
        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("rating_id")); // внешний ключ rating_id
        film.setMpa(mpa);

        return film;
    };

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        // Жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(film.getId(), film.getGenres());
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        // Жанры
        deleteFilmGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            saveFilmGenres(film.getId(), film.getGenres());
        }

        return film;
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa_rating m ON f.rating_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = filmRowMapper.mapRow(rs, rowNum);
            film.getMpa().setName(rs.getString("mpa_name"));
            return film;
        });

        // Загружаем все жанры одним запросом
        loadAllFilmGenres(films);

        return films;
    }

    @Override
    public Optional<Film> getById(Long id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa_rating m ON f.rating_id = m.id WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = filmRowMapper.mapRow(rs, rowNum);
            film.getMpa().setName(rs.getString("mpa_name"));
            return film;
        }, id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);
        // Используем оптимизированную загрузку жанров
        loadAllFilmGenres(List.of(film));
        return Optional.of(film);
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Long> getLikes(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, filmId);
    }

    private void saveFilmGenres(Long filmId, Set<Genre> genres) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)"; // таблица film_genre
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    private void deleteFilmGenres(Long filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Set<Genre> loadFilmGenres(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genre g JOIN film_genre fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("id")); // поле id
            genre.setName(rs.getString("name"));
            return genre;
        }, filmId);
        return new HashSet<>(genres);
    }

    private void loadAllFilmGenres(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        // Создаем список ID фильмов для IN запроса
        String filmIds = films.stream()
                .map(f -> String.valueOf(f.getId()))
                .reduce((a, b) -> a + "," + b)
                .orElse("");

        if (filmIds.isEmpty()) {
            return;
        }

        // Один запрос для получения всех жанров всех фильмов
        String sql = "SELECT fg.film_id, g.id as genre_id, g.name as genre_name " +
                "FROM film_genre fg " +
                "JOIN genre g ON fg.genre_id = g.id " +
                "WHERE fg.film_id IN (" + filmIds + ")";

        List<FilmGenreMapping> mappings = jdbcTemplate.query(sql, (rs, rowNum) -> {
            FilmGenreMapping mapping = new FilmGenreMapping();
            mapping.filmId = rs.getLong("film_id");
            mapping.genreId = rs.getLong("genre_id");
            mapping.genreName = rs.getString("genre_name");
            return mapping;
        });

        // Группируем жанры по фильмам
        for (Film film : films) {
            Set<Genre> filmGenres = new HashSet<>();
            for (FilmGenreMapping mapping : mappings) {
                if (mapping.filmId.equals(film.getId())) {
                    Genre genre = new Genre();
                    genre.setId(mapping.genreId);
                    genre.setName(mapping.genreName);
                    filmGenres.add(genre);
                }
            }
            film.setGenres(filmGenres);
        }
    }

    // Вспомогательный класс для маппинга
    private static class FilmGenreMapping {
        Long filmId;
        Long genreId;
        String genreName;
    }
}
