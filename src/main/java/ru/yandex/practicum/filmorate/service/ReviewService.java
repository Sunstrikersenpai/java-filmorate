package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventOperation;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public ReviewService(
            ReviewStorage reviewStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            EventService eventService
    ) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventService = eventService;
    }

    public Review addReview(Review review) {
        getUserById(review.getUserId());
        getFilmById(review.getFilmId());
        reviewStorage.addReview(review);
        eventService.logEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, EventOperation.ADD);
        return review;
    }

    public Review updateReview(Review review) {
        getUserById(review.getUserId());
        getFilmById(review.getFilmId());
        Review review1 = reviewStorage.updateReview(review);
        eventService.logEvent(review1.getUserId(), review1.getReviewId(), EventType.REVIEW, EventOperation.UPDATE);
        return review1;
    }

    public void deleteReview(Long id) {
        Review review = getReviewById(id);
        reviewStorage.deleteReview(id);
        eventService.logEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, EventOperation.REMOVE);
    }

    public Review getReview(Long id) {
        return reviewStorage.getReview(id).orElseThrow(() -> new NotFoundException("Review not found"));
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        getUserById(userId);
        getReviewById(reviewId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        getUserById(userId);
        getReviewById(reviewId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        getUserById(userId);
        getReviewById(reviewId);
        reviewStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        getUserById(userId);
        getReviewById(reviewId);
        reviewStorage.removeDislike(reviewId, userId);
    }

    private User getUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Validation error");
        }
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User с ID " + userId + " не найден"));
    }

    private Review getReviewById(Long reviewId) {
        return reviewStorage.getReview(reviewId).orElseThrow(() -> new NotFoundException("Review not found"));
    }

    private Film getFilmById(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Validation error");
        }
        return filmStorage.getFilm(filmId).orElseThrow(() -> new NotFoundException("Film not found"));
    }
}