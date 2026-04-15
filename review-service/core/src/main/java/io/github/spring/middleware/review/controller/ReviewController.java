package io.github.spring.middleware.review.controller;

import io.github.spring.middleware.annotation.Register;
import io.github.spring.middleware.review.api.ReviewApi;
import io.github.spring.middleware.review.domain.Review;
import io.github.spring.middleware.review.dto.PagedReviewResponseDto;
import io.github.spring.middleware.review.dto.ReviewBulkCreateRequestDto;
import io.github.spring.middleware.review.dto.ReviewBulkDeleteRequestDto;
import io.github.spring.middleware.review.dto.ReviewBulkReplaceRequestDto;
import io.github.spring.middleware.review.dto.ReviewCreateRequestDto;
import io.github.spring.middleware.review.dto.ReviewDto;
import io.github.spring.middleware.review.dto.ReviewPatchRequestDto;
import io.github.spring.middleware.review.dto.ReviewUpdateRequestDto;
import io.github.spring.middleware.review.mapper.ReviewMapper;
import io.github.spring.middleware.review.service.ReviewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Register(name = "review")
@AllArgsConstructor
public class ReviewController implements ReviewApi {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewDto createReview(ReviewCreateRequestDto reviewCreateRequestDto) {
        log.info("Received request to create review: {}", reviewCreateRequestDto);
        Review review = reviewMapper.toDomain(reviewCreateRequestDto);
        review = reviewService.createReview(review);
        return reviewMapper.toDto(review);
    }

    @Override
    public List<ReviewDto> createReviews(ReviewBulkCreateRequestDto reviewBulkCreateRequestDto) {
        log.info("Received request to create reviews in bulk: {}", reviewBulkCreateRequestDto);
        List<Review> reviews = reviewBulkCreateRequestDto.getItems().stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
        List<Review> createdReviews = reviewService.createReviewsForProduct(reviews, reviewBulkCreateRequestDto.getProductId());
        return createdReviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteReview(UUID id) {
        log.info("Received request to delete review with id: {}", id);
        reviewService.deleteReview(id);
    }

    @Override
    public void deleteReviews(ReviewBulkDeleteRequestDto reviewBulkDeleteRequestDto) {
        log.info("Received request to delete reviews in bulk: {}", reviewBulkDeleteRequestDto);
        reviewService.deleteReviewsFromProduct(reviewBulkDeleteRequestDto.getReviewIds(), reviewBulkDeleteRequestDto.getProductId());
    }

    @Override
    public ReviewDto getReview(UUID id) {
        log.info("Received request to get review with id: {}", id);
        Review review = reviewService.getReview(id);
        return reviewMapper.toDto(review);
    }

    @Override
    public PagedReviewResponseDto listReviews(UUID productId, Integer page, Integer size, String sort) {
        log.info("Rest request to list reviews. productId: {}, page: {}, size: {}", productId, page, size);
        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                sort == null || sort.isBlank() ? Sort.unsorted() : Sort.by(sort.split(","))
        );

        Page<Review> reviewsPage = reviewService.listReviews(null, productId, pageable);

        PagedReviewResponseDto response = new PagedReviewResponseDto();
        response.setContent(reviewsPage.getContent().stream().map(reviewMapper::toDto).collect(Collectors.toList()));
        response.setNumber(reviewsPage.getNumber());
        response.setSize(reviewsPage.getSize());
        response.setTotalElements((int) reviewsPage.getTotalElements());
        response.setTotalPages(reviewsPage.getTotalPages());

        return response;
    }

    @Override
    public ReviewDto patchReview(UUID id, ReviewPatchRequestDto reviewPatchRequestDto) {
        log.info("Received request to patch review with id: {}", id);
        Review review = reviewMapper.toDomain(reviewPatchRequestDto);
        Review patchedReview = reviewService.patchReview(id, review);
        return reviewMapper.toDto(patchedReview);
    }

    @Override
    public ReviewDto replaceReview(UUID id, ReviewUpdateRequestDto reviewUpdateRequestDto) {
        log.info("Received request to replace review with id: {}", id);
        Review review = reviewMapper.toDomain(reviewUpdateRequestDto);
        Review replacedReview = reviewService.replaceReview(id, review);
        return reviewMapper.toDto(replacedReview);
    }

    @Override
    public List<ReviewDto> replaceReviews(ReviewBulkReplaceRequestDto reviewBulkReplaceRequestDto) {
        log.info("Received request to replace reviews in bulk: {}", reviewBulkReplaceRequestDto);
        List<Review> reviews = reviewBulkReplaceRequestDto.getItems().stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
        List<Review> replacedReviews = reviewService.replaceReviewsForProduct(reviews, reviewBulkReplaceRequestDto.getProductId());
        return replacedReviews.stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }
}
