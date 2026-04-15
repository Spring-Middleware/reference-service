package io.github.spring.middleware.review.service;

import io.github.spring.middleware.review.domain.Review;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    Review createReview(Review review);

    List<Review> createReviewsForProduct(List<Review> reviews, UUID productId);

    Review getReview(UUID id);

    Review replaceReview(UUID id, Review review);

    List<Review> getReviewsByIds(List<UUID> reviewIds);

    List<Review> replaceReviewsForProduct(List<Review> reviews, UUID productId);

    Review patchReview(UUID id, Review review);

    Page<Review> listReviews(String q, UUID productId, Pageable pageable);

    void deleteReview(UUID id);

    void deleteReviewsFromProduct(List<UUID> ids, UUID productId);

}
