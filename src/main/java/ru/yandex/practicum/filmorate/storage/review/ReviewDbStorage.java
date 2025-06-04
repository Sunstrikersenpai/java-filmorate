package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.mapper.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

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
        String sql = "UPDATE reviews SET is_positive = ?, content = ? WHERE review_id = ?";
        jdbcTemplate.update(
                sql,
                review.getIsPositive(),
                review.getContent(),
                review.getReviewId()
        );

        return getReview(review.getReviewId()).orElseThrow(() -> new NotFoundException("not found"));
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
    public Optional<Review> getReview(Long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> reviews = jdbcTemplate.query(sql, reviewRowMapper, id);
        return reviews.isEmpty() ? Optional.empty() : Optional.of(reviews.getFirst());
    }

    @Override
    public List<Review> getReviews(Long filmId, Integer count) {
        String sql;
        Object[] param;

        if (filmId == null) {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            param = new Object[]{count};
        } else {
            sql = sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
            param = new Object[]{filmId, count};
        }

        return jdbcTemplate.query(sql, reviewRowMapper, param);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        reviewExist(reviewId);

        Boolean currentVote = getCurrentVote(reviewId, userId);

        if (currentVote == null) {
            insertVote(reviewId, userId, true, 1);
        } else if (!currentVote) {
            setVote(reviewId, userId, true, 2);
        }
    }


    @Override
    public void removeLike(Long reviewId, Long userId) {
        reviewExist(reviewId);

        Boolean currentVote = getCurrentVote(reviewId, userId);

        if (Boolean.TRUE.equals(currentVote)) {
            deleteVote(reviewId, userId, -1);
        }
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        reviewExist(reviewId);

        Boolean currentVote = getCurrentVote(reviewId, userId);

        if (currentVote == null) {
            insertVote(reviewId, userId, false, -1);
        } else if (currentVote) {
            setVote(reviewId, userId, false, -2);
        }
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        reviewExist(reviewId);

        Boolean currentVote = getCurrentVote(reviewId, userId);

        if (Boolean.FALSE.equals(currentVote)) {
            deleteVote(reviewId, userId, 1);
        }
    }

    public void reviewExist(Long reviewId) {
        Optional<Review> review = getReview(reviewId);
        if (review.isEmpty()) {
            throw new NotFoundException("Review not found");
        }
    }

    private void setVote(Long reviewId, Long userId, boolean isPositive, int usefulDelta) {
        String updateSql = "UPDATE review_likes SET is_positive = ? WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(updateSql, isPositive, reviewId, userId);

        String updateUsefulSql = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
        jdbcTemplate.update(updateUsefulSql, usefulDelta, reviewId);
    }

    private void insertVote(Long reviewId, Long userId, boolean isPositive, int usefulDelta) {
        String insertSql = "INSERT INTO review_likes (review_id, user_id, is_positive) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, reviewId, userId, isPositive);

        String updateUsefulSql = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
        jdbcTemplate.update(updateUsefulSql, usefulDelta, reviewId);
    }

    private void deleteVote(Long reviewId, Long userId, int usefulDelta) {
        String deleteSql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(deleteSql, reviewId, userId);

        String updateUsefulSql = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
        jdbcTemplate.update(updateUsefulSql, usefulDelta, reviewId);
    }

    private Boolean getCurrentVote(Long reviewId, Long userId) {
        String selectSql = "SELECT is_positive FROM review_likes WHERE review_id = ? AND user_id = ?";
        return jdbcTemplate.query(
                selectSql,
                rs -> rs.next() ? rs.getBoolean("is_positive") : null,
                reviewId, userId
        );
    }
}
