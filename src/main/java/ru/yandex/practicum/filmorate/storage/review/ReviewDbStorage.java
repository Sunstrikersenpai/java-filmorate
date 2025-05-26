package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;


    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO reviews (user_id, film_id, is_positive, content) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, review.getUserId());
            ps.setLong(2, review.getFilmId());
            ps.setBoolean(3, review.getIsPositive());
            ps.setString(4, review.getContent());
            return ps;
        }, keyHolder);

        if (rows == 0) {
            throw new ValidationException("Review not inserted");
        }

        Long generatedId = keyHolder.getKey().longValue();
        review.setReviewId(generatedId);
        review.setUseful(0);

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET is_positive = ?, content = ? WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql,
                review.getIsPositive(),
                review.getContent(),
                review.getUserId(),
                review.getFilmId());

        return review;
    }

    @Override
    public void deleteReview(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        int rows = jdbcTemplate.update(sql, id);
        if (rows == 0) {
            throw new NotFoundException("Review not found");
        }
    }

    @Override
    public Review getReview(Long id) {
        return null;
    }

    @Override
    public List<Review> getReviews(Long filmId, Integer count) {
        return List.of();
    }

    @Override
    public void addLike(Long reviewId, Long userId) {

    }

    @Override
    public void addDislike(Long reviewId, Long userId) {

    }

    @Override
    public void removeLike(Long reviewId, Long userId) {

    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {

    }
}
