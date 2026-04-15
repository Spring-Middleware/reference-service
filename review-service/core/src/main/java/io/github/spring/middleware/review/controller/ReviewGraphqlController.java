package io.github.spring.middleware.review.controller;

import io.github.spring.middleware.graphql.annotations.GraphQLService;
import io.github.spring.middleware.review.domain.Review;
import io.github.spring.middleware.review.dto.graphql.ReviewInput;
import io.github.spring.middleware.review.mapper.ReviewMapper;
import io.github.spring.middleware.review.service.ReviewService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@GraphQLService
public class ReviewGraphqlController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    public ReviewGraphqlController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    @GraphQLQuery(name = "review")
    public Review getReview(@GraphQLArgument(name = "id") UUID id) {
        return reviewService.getReview(id);
    }

    @GraphQLQuery(name = "reviews")
    public Page<Review> listReviews(
            @GraphQLArgument(name = "q") String q,
            @GraphQLArgument(name = "productId") UUID productId,
            @GraphQLArgument(name = "page") Integer page,
            @GraphQLArgument(name = "size") Integer size,
            @GraphQLArgument(name = "sort") String sort) {

        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                sort != null ? Sort.by(sort.split(",")) : Sort.unsorted()
        );
        return reviewService.listReviews(q, productId, pageable);
    }

    @GraphQLQuery(name = "reviewsByIds")
    public List<Review> getReviewsByIds(@GraphQLArgument(name = "ids") List<UUID> ids) {
        return reviewService.getReviewsByIds(ids);
    }

    @GraphQLMutation(name = "createReview")
    public Review createReview(@GraphQLArgument(name = "input") ReviewInput input) {
        Review review = reviewMapper.toDomain(input);
        return reviewService.createReview(review);
    }

    @GraphQLMutation(name = "createReviewsForProduct")
    public List<Review> createReviewsForProduct(
            @GraphQLArgument(name = "productId") UUID productId,
            @GraphQLArgument(name = "inputs") List<ReviewInput> inputs) {
        List<Review> reviews = inputs.stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
        return reviewService.createReviewsForProduct(reviews, productId);
    }

    @GraphQLMutation(name = "replaceReview")
    public Review replaceReview(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") ReviewInput input) {
        Review review = reviewMapper.toDomain(input);
        return reviewService.replaceReview(id, review);
    }

    @GraphQLMutation(name = "patchReview")
    public Review patchReview(
            @GraphQLArgument(name = "id") UUID id,
            @GraphQLArgument(name = "input") ReviewInput input) {
        Review review = reviewMapper.toDomain(input);
        return reviewService.patchReview(id, review);
    }

    @GraphQLMutation(name = "deleteReview")
    public void deleteReview(@GraphQLArgument(name = "id") UUID id) {
        reviewService.deleteReview(id);
    }

    @GraphQLMutation(name = "deleteReviewsFromProduct")
    public void deleteReviewsFromProduct(
            @GraphQLArgument(name = "productId") UUID productId,
            @GraphQLArgument(name = "ids") List<UUID> ids) {
        reviewService.deleteReviewsFromProduct(ids, productId);
    }
}

