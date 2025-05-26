package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collections;
import java.util.List;

@Service
public class ReviewService {

    public Review addReview(Review review) {
        return null;
    }

    public Review updateReview(Review review) {
        return null;
    }

    public void deleteReview(Long id) {
    }

    public Review getReview(Long id) {
        return null;
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        return Collections.emptyList();
    }

    public void addLike(Long reviewId, Long userId) {
    }

    public void addDislike(Long reviewId, Long userId) {
    }

    public void removeLike(Long reviewId, Long userId) {
    }

    public void removeDislike(Long reviewId, Long userId) {
    }
}

