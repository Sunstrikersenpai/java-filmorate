package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRowMapper mpaRowMapper;

    public List<Mpa> findAll() {
        String sql = "SELECT * FROM mpa ORDER BY mpa_id ASC";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    public Optional<Mpa> findById(int id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        List<Mpa> mpa = jdbcTemplate.query(sql, mpaRowMapper, id);
        return mpa.stream().findFirst();
    }

    public boolean existsById(int mpaId) {
        String sql = "SELECT COUNT(*) FROM mpa WHERE mpa_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count != null && count > 0;
    }
}