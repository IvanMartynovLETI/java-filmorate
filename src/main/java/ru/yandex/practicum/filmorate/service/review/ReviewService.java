package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FeedStorage feedStorage;

    public Review addReview(Review review) {
        Review reviewInStorage = reviewStorage.addReview(review);
        feedStorage.addFeedList(reviewInStorage.getUserId(), reviewInStorage.getReviewId(),
                EventType.REVIEW, Operation.ADD);
        return reviewInStorage;
    }

    public Review modifyReview(Review review) {
        Review reviewInStorage = reviewStorage.modifyReview(review);
        feedStorage.addFeedList(reviewInStorage.getUserId(), reviewInStorage.getReviewId(),
                EventType.REVIEW, Operation.UPDATE);
        return reviewInStorage;
    }

    public Review deleteReviewById(Long id) {
        Review reviewInStorage = reviewStorage.deleteReviewById(id);
        feedStorage.addFeedList(id, reviewInStorage.getReviewId(),
                EventType.REVIEW, Operation.REMOVE);
        return reviewInStorage;
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
