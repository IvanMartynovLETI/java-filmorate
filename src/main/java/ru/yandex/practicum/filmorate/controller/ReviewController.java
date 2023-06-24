package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("Request for review adding obtained.");

        return reviewService.addReview(review);
    }

    @PutMapping
    public Review put(@RequestBody Review review) {
        log.info("Request for review modification obtained.");

        return reviewService.modifyReview(review);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Review deleteReviewById(@PathVariable final Long id) {
        log.info("Request for review deletion obtained.");

        return reviewService.deleteReviewById(id);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Review getReviewById(@PathVariable final Long id) {
        log.info("Request for getting review by id obtained.");

        return reviewService.getReviewById(id);
    }

    @GetMapping()
    public Optional<List<Review>> getReviews(@RequestParam(required = false) Long filmId,
                                             @RequestParam(required = false) Integer count) {
        log.info("Request for list of reviews  obtained.");

        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseBody
    public Review setLikeToReview(@PathVariable(required = false) final Long id,
                                  @PathVariable(required = false) final Long userId) {
        log.info("Request for setting like to review obtained.");

        return reviewService.setLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseBody
    public Review setDisLikeToReview(@PathVariable(required = false) final Long id,
                                     @PathVariable(required = false) final Long userId) {
        log.info("Request for setting dislike to review obtained.");

        return reviewService.setDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseBody
    public Review deleteLikeFromReview(@PathVariable(required = false) final Long id,
                                       @PathVariable(required = false) final Long userId) {
        log.info("Request for deletion like from review obtained.");

        return reviewService.deleteLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseBody
    public Review deleteDislikeFromReview(@PathVariable(required = false) final Long id,
                                          @PathVariable(required = false) final Long userId) {
        log.info("Request for deletion dislike from review obtained.");

        return reviewService.deleteDislikeFromReview(id, userId);
    }
}
