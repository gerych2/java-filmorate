package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

@Component("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Mpa> mpaRowMapper = (ResultSet rs, int rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getLong("id"));   // колонка id из schema.sql
        mpa.setName(rs.getString("name"));
        return mpa;
    };

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM mpa_rating ORDER BY id"; // правильное имя таблицы
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    @Override
    public Optional<Mpa> getById(Long id) {
        String sql = "SELECT * FROM mpa_rating WHERE id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, mpaRowMapper, id);
        return mpaList.isEmpty() ? Optional.empty() : Optional.of(mpaList.get(0));
    }
}
