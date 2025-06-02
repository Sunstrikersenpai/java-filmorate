package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.FilmSortBy;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private final GenreRowMapper genreRowMapper;
    private final DirectorRowMapper directorRowMapper;

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
            Set<Director> directors = loadDirectorByFilmId(film.getId());
            film.setDirectors(directors);
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
        addDirectorToFilm(film);

        log.warn("ПРОВЕРКА: Film create : " + film);

        return film;
    }

    @Override
    public Film update(Film film) {

        log.warn("ПРОВЕРКА: Film данные для update : " + film);

        String sql = "UPDATE films SET name = ?, description = ?, duration = ?, release_date = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                Date.valueOf(film.getReleaseDate()),
                film.getMpa().getId(),
                film.getId());

        addGenresToFilm(film);
        addDirectorToFilm(film);

        log.warn("ПРОВЕРКА: Film после update : " + film);
// ДОБАВИТЬ: нет заполнения , mpa=Mpa(id=2, name=null)


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
        addLinkedDataToFilm(film);

        log.warn("ПРОВЕРКА: Optional<Film> getFilm : " + film);


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
            addLinkedDataToFilm(film);
            return film;
        }, count);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, " +
                "COUNT(l3.user_id) AS likes_count " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "JOIN likes l1 ON f.film_id = l1.film_id " +
                "JOIN likes l2 ON f.film_id = l2.film_id " +
                "JOIN likes l3 ON f.film_id = l3.film_id " +
                "WHERE l1.user_id = ? AND l2.user_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC";

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, userId, friendId);

        for (Film film : films) {
            addLinkedDataToFilm(film);
        }

        return films;
    }


    public List<Film> getFilmsOfDirectorSortedByParams(Long directorID, FilmSortBy sortBy) {

        String orderQuery = switch (sortBy) {
            case YEAR -> "f.release_date ASC";
            case LIKES -> "likes_count DESC";
        };


        String query = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, " +
                "f.mpa_id, m.name AS mpa_name, " +
                "COUNT(l.user_id) AS likes_count " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.film_id, f.name, f.description, f.duration, f.release_date, f.mpa_id, m.name " +
                "ORDER BY " + orderQuery;


        List<Film> filmList = jdbcTemplate.query(query, filmRowMapper, directorID);
        log.warn("ПРОВЕРКА: getFilmsOfDirectorSortedByParams ДО заполнения film = " + filmList);

        for (Film film : filmList) {
//            film.setUsersLikes(new HashSet<>());
            addLinkedDataToFilm(film);
        }

        log.warn("ПРОВЕРКА: getFilmsOfDirectorSortedByParams ПОСЛЕ заполнения film = " + filmList);
        return filmList;

    }

    public List<Film> getFilmsBySearchCriteria(String query, Set<String> searchCriteria) {

        boolean searchByTitle = searchCriteria.contains("title");
        boolean searchByDirector = searchCriteria.contains("director");

        List<Film> filmList = new ArrayList<>();

        if (searchByTitle && searchByDirector) {
            String sql = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, " +
                    "f.mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "WHERE f.name LIKE ? " +
                    "UNION " +
                    " SELECT f.film_id, f.name, f.description, f.duration, f.release_date, " +
                    "f.mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "JOIN film_directors fd ON f.film_id = fd.film_id " +
                    "JOIN directors d ON fd.director_id = d.director_id " +
                    "WHERE d.name LIKE ?";

            filmList = jdbcTemplate.query(sql, filmRowMapper, "%" + query + "%", "%" + query + "%");
        } else if (searchByTitle) {
            String sql = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, " +
                    "f.mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "WHERE f.name LIKE ?";

            filmList = jdbcTemplate.query(sql, filmRowMapper, "%" + query + "%");
        } else if (searchByDirector) {
            String sql = "SELECT f.film_id, f.name, f.description, f.duration, f.release_date, " +
                    "f.mpa_id, m.name AS mpa_name " +
                    "FROM films f " +
                    "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "JOIN film_directors fd ON f.film_id = fd.film_id " +
                    "JOIN directors d ON fd.director_id = d.director_id " +
                    "WHERE d.name LIKE ?";

            filmList = jdbcTemplate.query(sql, filmRowMapper, "%" + query + "%");
        }

        log.warn("ПРОВЕРКА: getFilmsBySearchCriteria ДО заполнения filmList = " + filmList);

        for (Film film : filmList) {
            addLinkedDataToFilm(film);
        }

        log.warn("ПРОВЕРКА: getFilmsBySearchCriteria ПОСЛЕ заполнения filmList = " + filmList);

        return filmList;
    }

    private Set<Genre> loadGenresByFilmId(Long filmId) {
        String genresSql = "SELECT g.genre_id, g.name " +
                "FROM film_genre fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY genre_id ASC ";
        List<Genre> genres = jdbcTemplate.query(genresSql, genreRowMapper, filmId);

        log.warn("ПРОВЕРКА: loadGenresByFilmId genres = " + genres );

        return genres.isEmpty() ? new HashSet<>() : new HashSet<>(genres);
    }

    private void addGenresToFilm(Film film) {

        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";

        log.warn("ПРОВЕРКА: addGenresToFilm deleteSql = " + "DELETE FROM film_genre WHERE film_id = {}", film.getId() );

        jdbcTemplate.update(deleteSql, film.getId());


        log.warn("ПРОВЕРКА: film.getGenres() = " + film.getGenres() );
        log.warn("ПРОВЕРКА: film.getGenres().isEmpty() = " + film.getGenres().isEmpty() );

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : film.getGenres()) {
            log.warn("ПРОВЕРКА: addGenresToFilm deleteSql = " + "INSERT INTO film_genre (film_id, genre_id) VALUES ({}, {})", film.getId(), genre.getId() );
            jdbcTemplate.update(insertSql, film.getId(), genre.getId());
        }
    }

    @Override
    public void removeFilmById(Long filmId) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }


    private Set<Director> loadDirectorByFilmId(Long filmId) {
        String sql = "SELECT d.director_id, d.name " +
                "FROM film_directors fd " +
                "JOIN directors d ON fd.director_id = d.director_id " +
                "WHERE fd.film_id = ?";
        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper, filmId);
        return directors.isEmpty() ? new HashSet<>() : new HashSet<>(directors);
    }

    private void addDirectorToFilm(Film film) {

        String deleteSql = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

        for (Director director : film.getDirectors()) {
            jdbcTemplate.update(sql, film.getId(), director.getId());
        }
    }

    private Film addLinkedDataToFilm(Film film) {
        Set<Genre> genres = loadGenresByFilmId(film.getId());
        film.setGenres(genres);
        Set<Director> directors = loadDirectorByFilmId(film.getId());
        film.setDirectors(directors);
        return film;
    }

}