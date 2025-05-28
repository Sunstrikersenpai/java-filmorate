package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(
            ReviewStorage reviewStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage
    ) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review addReview(Review review) {
        validateReview(review);
        getUserById(review.getUserId());
        getFilmById(review.getFilmId());
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        getUserById(review.getUserId());
        getFilmById(review.getFilmId());
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        reviewStorage.deleteReview(id);
    }

    public Review getReview(Long id) {
        return reviewStorage.getReview(id).orElseThrow(() -> new NotFoundException("Review not found"));
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        getUserById(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        getUserById(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        getUserById(userId);
        reviewStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        getUserById(userId);
        reviewStorage.removeDislike(reviewId, userId);
    }

    private User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User с ID " + userId + " не найден"));
    }

    private Film getFilmById(Long filmId) {
        return filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
    }

    private void validateReview(Review review) {
        if (review.getUserId() == null) {
            throw new ValidationException("User ID must not be null");
        }
        if (review.getFilmId() == null) {
            throw new ValidationException("Film ID must not be null");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Field 'isPositive' must not be null");
        }
    }
}