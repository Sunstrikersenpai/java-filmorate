package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, f.mpa_id, m.name AS mpa_name " +
                "FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        String genresSql = "SELECT fg.film_id, g.genre_id, g.name FROM film_genre fg JOIN genres g ON fg.genre_id = g.genre_id";
        Map<Long, Set<Genre>> filmGenres = new HashMap<>();
        jdbcTemplate.query(genresSql, rs -> {
            long filmId = rs.getLong("film_id");
            Genre genre = genreRowMapper.mapRow(rs, rs.getRow());
            filmGenres.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
        });

        for (Film film : films) {
            Set<Genre> genres = filmGenres.getOrDefault(film.getId(), Collections.emptySet());
            film.setGenres(genres);
        }

        return films;
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films (name, description, duration, release_date, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);
        addGenresToFilm(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                Date.valueOf(film.getReleaseDate()),
                film.getMpa().getId(),
                film.getId());

        addGenresToFilm(film);
        return film;
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        String sql = """
                SELECT f.film_id, f.name, f.description, f.duration, f.release_date, f.mpa_id, m.name AS mpa_name
                FROM films f
                LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
                WHERE f.film_id = ?
                """;

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        Film film = films.get(0);
        Set<Genre> genres = loadGenresByFilmId(film.getId());
        film.setGenres(genres);

        return Optional.of(film);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopular(Long count) {
        String query = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, f.mpa_id, m.name AS mpa_name, COUNT(l.user_id) AS likes_count " +
                "FROM films AS f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            Film film = filmRowMapper.mapRow(rs, rowNum);
            film.setUsersLikes(new HashSet<>());
            return film;
        }, count);
    }

    private Set<Genre> loadGenresByFilmId(Long filmId) {
        String genresSql = "SELECT g.genre_id, g.name FROM film_genre fg JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genresSql, genreRowMapper, filmId);
        return genres.isEmpty() ? new HashSet<>() : new HashSet<>(genres);
    }

    private void addGenresToFilm(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(insertSql, film.getId(), genre.getId());
        }
    }
}