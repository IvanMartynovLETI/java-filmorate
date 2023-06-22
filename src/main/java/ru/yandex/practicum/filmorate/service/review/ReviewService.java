package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review modifyReview(Review review) {
        return reviewStorage.modifyReview(review);
    }

    public Review deleteReviewById(Long id) {
        return reviewStorage.deleteReviewById(id);
    }

    public Review getReviewById(Long id) {
        return reviewStorage.getReviewById(id);
    }

    public Optional<List<Review>> getReviews(Long filmId, Integer count) {
        return reviewStorage.getReviews(filmId, count);
    }

    public Review setLikeToReview(Long reviewId, Long userId) {
        return reviewStorage.setLikeToReview(reviewId, userId);
    }

    public Review setDislikeToReview(Long reviewId, Long userId) {
        return reviewStorage.setDislikeToReview(reviewId, userId);
    }

    public Review deleteLikeFromReview(Long reviewId, Long userId) {
        return reviewStorage.deleteLikeFromReview(reviewId, userId);
    }

    public Review deleteDislikeFromReview(Long reviewId, Long userId) {
        return reviewStorage.deleteDislikeFromReview(reviewId, userId);
    }
}
