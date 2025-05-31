package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class})
@ActiveProfiles("test")
@Sql(scripts = "classpath:schema.sql")
@Sql(scripts = "classpath:test.sql")
class FilmDbTest {

    private final FilmDbStorage filmStorage;

    @Test
    @DirtiesContext
    public void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.getFilm(1L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(1L);
                    assertThat(film.getName()).isEqualTo("Inception");
                    assertThat(film.getMpa().getName()).isEqualTo("R");
                });
    }

    @Test
    @DirtiesContext
    public void testFindAllFilms() {
        List<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(3);
    }

    @Test
    @DirtiesContext
    public void testGetPopularFilms() {
        List<Film> popularFilms = filmStorage.getPopular(2L, 2L, 2000L);
        assertThat(popularFilms).hasSize(1);
        assertThat(popularFilms.get(0).getName()).isEqualTo("Titanic");
    }
}
