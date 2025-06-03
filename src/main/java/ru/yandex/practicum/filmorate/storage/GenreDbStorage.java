package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper = new GenreRowMapper();

    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres ORDER BY genre_id ASC";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    public Optional<Genre> findById(int id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, id);
        return genres.stream().findFirst();
    }

    public List<Integer> findExistingIds(List<Integer> ids) {
        String sql = "SELECT genre_id FROM genres WHERE genre_id IN (" +
                String.join(",", ids.stream().map(String::valueOf).toArray(String[]::new)) +
                ")";
        return new ArrayList<>(jdbcTemplate.queryForList(sql, Integer.class));
    }
}