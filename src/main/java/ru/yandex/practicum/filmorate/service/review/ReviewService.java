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
        log.info("Service layer: add review  for film with id: '{}'.", review.getFilmId());

        Review reviewInStorage = reviewStorage.addReview(review);
        feedStorage.addFeedList(reviewInStorage.getUserId(), reviewInStorage.getReviewId(),
                EventType.REVIEW, Operation.ADD);

        return reviewInStorage;
    }

    public Review modifyReview(Review review) {
        log.info("Service layer: modify review with id: '{}'.", review.getReviewId());

        Review reviewInStorage = reviewStorage.modifyReview(review);
        feedStorage.addFeedList(reviewInStorage.getUserId(), reviewInStorage.getReviewId(),
                EventType.REVIEW, Operation.UPDATE);

        return reviewInStorage;
    }

    public Review deleteReviewById(Long id) {
        log.info("Service layer: delete review with id: '{}'.", id);

        Review reviewInStorage = reviewStorage.deleteReviewById(id);
        feedStorage.addFeedList(id, reviewInStorage.getReviewId(),
                EventType.REVIEW, Operation.REMOVE);

        return reviewInStorage;
    }

    public Review getReviewById(Long id) {
        log.info("Service layer: get review with id: '{}'.", id);

        return reviewStorage.getReviewById(id);
    }

    public Optional<List<Review>> getReviews(Long filmId, Integer count) {
        log.info("Service layer: get reviews.");

        return reviewStorage.getReviews(filmId, count);
    }

    public Review setLikeToReview(Long reviewId, Long userId) {
        log.info("Service layer: set like to review with id: '{}' from user with id: '{}'.", reviewId, userId);

        return reviewStorage.setLikeToReview(reviewId, userId);
    }

    public Review setDislikeToReview(Long reviewId, Long userId) {
        log.info("Service layer: set dislike to review with id: '{}' from user with id: '{}'.", reviewId, userId);

        return reviewStorage.setDislikeToReview(reviewId, userId);
    }

    public Review deleteLikeFromReview(Long reviewId, Long userId) {
        log.info("Service layer: delete like to review with id: '{}' from user with id: '{}'.", reviewId, userId);

        return reviewStorage.deleteLikeFromReview(reviewId, userId);
    }

    public Review deleteDislikeFromReview(Long reviewId, Long userId) {
        log.info("Service layer: delete dislike to review with id: '{}' from user with id: '{}'.", reviewId, userId);

        return reviewStorage.deleteDislikeFromReview(reviewId, userId);
    }
}
