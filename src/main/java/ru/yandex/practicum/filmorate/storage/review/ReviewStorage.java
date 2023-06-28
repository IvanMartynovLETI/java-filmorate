package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review addReview(Review review);

    Review modifyReview(Review review);

    Review deleteReviewById(Long id);

    Review getReviewById(Long id);

    Optional<List<Review>> getReviews(Long filmId, Integer count);

    Review setLikeToReview(Long reviewId, Long userId);

    Review setDislikeToReview(Long reviewId, Long userId);

    Review deleteLikeFromReview(Long reviewId, Long userId);

    Review deleteDislikeFromReview(Long reviewId, Long userId);
}

