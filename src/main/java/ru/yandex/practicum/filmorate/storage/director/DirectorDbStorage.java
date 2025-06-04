package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorRowMapper rowMapper = new DirectorRowMapper();

    @Override
    public List<Director> getDirectorList() {
        String sql = "SELECT d.director_id, d.name FROM directors d";

        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Director> getDirectorByID(Long directorID) {
        String sql = "SELECT d.director_id, d.name FROM directors d WHERE d.director_id = ?";
        List<Director> director = jdbcTemplate.query(sql, rowMapper, directorID);
        return director.stream().findFirst();
    }

    @Override
    public Director add(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return director;
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void delete(Long directorID) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        int rows = jdbcTemplate.update(sql, directorID);
        if (rows == 0) {
            throw new NotFoundException("Review not found");
        }
    }
}