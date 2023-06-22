package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Validator validator;
    private static final int REVIEWS_COUNT_BY_DEFAULT = 10;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate, Validator validator) {
        this.jdbcTemplate = jdbcTemplate;
        this.validator = validator;
    }

    @Override
    public Review addReview(Review review) {
        log.info("Request to database for film with id {} review creation obtained.", review.getFilmId());
        if (review.getUserId() <= 0) {
            String userWarning = "User with id: " + review.getUserId() + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        }
        String sqlQuery = "SELECT * from users WHERE users_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, review.getUserId());
        if (!userRow.next()) {
            String userWarning = "User with id: " + review.getUserId() + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        }
        if (review.getFilmId() <= 0) {
            String filmWarning = "Film with id: " + review.getFilmId() + " doesn't exist.";
            throw new EntityNotFoundException(filmWarning);
        }
        sqlQuery = "SELECT * from film WHERE film_id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, review.getFilmId());
        if (!filmRow.next()) {
            String filmWarning = "Film with id: " + review.getFilmId() + " doesn't exist.";
            throw new EntityNotFoundException(filmWarning);
        }
        Review checkedReview = validator.validateReviewInDataBase(review, jdbcTemplate, true);
        sqlQuery = "INSERT INTO reviews(content, is_positive, user_id, film_id) VALUES(?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery, checkedReview.getContent(), checkedReview.getIsPositive(),
                checkedReview.getUserId(), checkedReview.getFilmId());
        return getReviewById(checkedReview.getReviewId());
    }

    @Override
    public Review modifyReview(Review review) {
        log.info("Request to database for review with id '{}' update obtained.", review.getReviewId());
        Review checkedReview = validator.validateReviewInDataBase(review, jdbcTemplate, false);
        String sqlQuery = "UPDATE reviews SET content = ?, is_positive = ? WHERE reviews_id = ?";

        jdbcTemplate.update(sqlQuery, checkedReview.getContent(), checkedReview.getIsPositive(),
                checkedReview.getReviewId());

        return getReviewById(checkedReview.getReviewId());
    }

    @Override
    public Review deleteReviewById(Long id) {
        log.info("Request to database for review with id '{}' deletion obtained.", id);
        Review checkedReview = validator.validateReviewInDataBase(getReviewById(id), jdbcTemplate, false);

        jdbcTemplate.update("DELETE FROM reviews WHERE reviews_id = ?", checkedReview.getReviewId());

        return checkedReview;
    }

    @Override
    public Review getReviewById(Long id) {
        log.info("Request to database for obtaining review by id: {} obtained.", id);
        Review review = new Review();
        String sqlQuery = "SELECT * FROM reviews WHERE reviews_id = ?";

        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if (!reviewRow.next()) {
            String reviewWarning = "Review with id: " + id + " doesn't exist.";
            throw new EntityNotFoundException(reviewWarning);
        } else {
            review.setReviewId(reviewRow.getLong("reviews_id"));
            review.setContent(reviewRow.getString("content"));
            review.setIsPositive(reviewRow.getBoolean("is_positive"));
            review.setUserId(reviewRow.getLong("user_id"));
            review.setFilmId(reviewRow.getLong("film_id"));
            review.setUseful(reviewRow.getLong("useful"));
        }

        return review;
    }

    @Override
    public Optional<List<Review>> getReviews(Long filmId, Integer count) {
        log.info("Request to database for getting review's collection obtained.");
        String sqlQuery;
        List<Review> reviews;
        if (filmId == null) {
            if (count == null) {
                count = REVIEWS_COUNT_BY_DEFAULT;
                sqlQuery = "SELECT * FROM reviews GROUP BY reviews_id ORDER BY useful DESC LIMIT " + count;
                reviews = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getReviewById(rs.getLong(
                        "reviews_id")));
            } else {
                if (count <= 0) {
                    throw new IncorrectParameterException("'count' parameter must be positive.");
                } else {
                    sqlQuery = "SELECT * FROM reviews GROUP BY reviews_id ORDER BY reviews_id ASC LIMIT " + count;
                    reviews = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                            getReviewById(rs.getLong("reviews_id")));
                }
            }
        } else {
            sqlQuery = "SELECT content FROM reviews WHERE film_id = ?";
            SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
            if (!reviewRow.next()) {
                reviews = null;
            } else {
                if (count == null) {
                    count = REVIEWS_COUNT_BY_DEFAULT;
                    sqlQuery = "SELECT * FROM reviews WHERE film_id = " + filmId +
                            " GROUP BY reviews_id ORDER BY useful DESC LIMIT " + count;
                    reviews = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                            getReviewById(rs.getLong("reviews_id")));
                } else {
                    if (count <= 0) {
                        throw new IncorrectParameterException("'count' parameter must be positive.");
                    } else {
                        sqlQuery = "SELECT * FROM reviews WHERE film_id = " + filmId +
                                " GROUP BY reviews_id ORDER BY useful DESC LIMIT " + count;
                        reviews = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                                getReviewById(rs.getLong("reviews_id")));
                    }
                }
            }
        }
        return Optional.of(Objects.requireNonNullElseGet(reviews, ArrayList::new));
    }

    @Override
    public Review setLikeToReview(Long reviewId, Long userId) {
        if (reviewId == null) {
            throw new IncorrectParameterException("'reviewId' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }
        String sqlQuery = "SELECT * from reviews WHERE reviews_id = ?";
        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        if (!reviewRow.next()) {
            String reviewWarning = "Review with id: " + reviewId + " doesn't exist.";
            throw new EntityNotFoundException(reviewWarning);
        }
        sqlQuery = "SELECT * from reviews WHERE user_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (!userRow.next()) {
            String userWarning = "User with id: " + userId + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        }
        Long useful = getReviewById(reviewId).getUseful();
        useful++;

        sqlQuery = "UPDATE reviews SET useful = ? WHERE reviews_id = ?";
        jdbcTemplate.update(sqlQuery, useful, reviewId);

        return getReviewById(reviewId);
    }

    @Override
    public Review setDislikeToReview(Long reviewId, Long userId) {
        if (reviewId == null) {
            throw new IncorrectParameterException("'reviewId' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }
        String sqlQuery = "SELECT * from reviews WHERE reviews_id = ?";
        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        if (!reviewRow.next()) {
            String reviewWarning = "Review with id: " + reviewId + " doesn't exist.";
            throw new EntityNotFoundException(reviewWarning);
        }
        sqlQuery = "SELECT * from reviews WHERE user_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (!userRow.next()) {
            String userWarning = "User with id: " + userId + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        }
        Long useful = getReviewById(reviewId).getUseful();
        useful--;

        sqlQuery = "UPDATE reviews SET useful = ? WHERE reviews_id = ?";
        jdbcTemplate.update(sqlQuery, useful, reviewId);

        return getReviewById(reviewId);
    }

    @Override
    public Review deleteLikeFromReview(Long reviewId, Long userId) {
        if (reviewId == null) {
            throw new IncorrectParameterException("'reviewId' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }
        String sqlQuery = "SELECT * from reviews WHERE reviews_id = ?";
        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        if (!reviewRow.next()) {
            String reviewWarning = "Review with id: " + reviewId + " doesn't exist.";
            throw new EntityNotFoundException(reviewWarning);
        }
        sqlQuery = "SELECT * from reviews WHERE user_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (!userRow.next()) {
            String userWarning = "User with id: " + userId + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        }
        Long useful = getReviewById(reviewId).getUseful();
        useful--;

        sqlQuery = "UPDATE reviews SET useful = ? WHERE reviews_id = ?";
        jdbcTemplate.update(sqlQuery, useful, reviewId);

        return getReviewById(reviewId);
    }

    @Override
    public Review deleteDislikeFromReview(Long reviewId, Long userId) {
        if (reviewId == null) {
            throw new IncorrectParameterException("'reviewId' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }
        String sqlQuery = "SELECT * from reviews WHERE reviews_id = ?";
        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sqlQuery, reviewId);
        if (!reviewRow.next()) {
            String reviewWarning = "Review with id: " + reviewId + " doesn't exist.";
            throw new EntityNotFoundException(reviewWarning);
        }
        sqlQuery = "SELECT * from reviews WHERE user_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (!userRow.next()) {
            String userWarning = "User with id: " + userId + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        }
        Long useful = getReviewById(reviewId).getUseful();
        useful++;

        sqlQuery = "UPDATE reviews SET useful = ? WHERE reviews_id = ?";
        jdbcTemplate.update(sqlQuery, useful, reviewId);

        return getReviewById(reviewId);
    }
}