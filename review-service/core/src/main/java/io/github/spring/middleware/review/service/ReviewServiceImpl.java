package io.github.spring.middleware.review.service;

import io.github.spring.middleware.product.api.ProductApi;
import io.github.spring.middleware.product.dto.DigitalProductDto;
import io.github.spring.middleware.product.dto.DigitalProductPatchRequestDto;
import io.github.spring.middleware.product.dto.PhysicalProductDto;
import io.github.spring.middleware.product.dto.PhysicalProductPatchRequestDto;
import io.github.spring.middleware.product.dto.ProductDto;
import io.github.spring.middleware.product.dto.ProductPatchRequestDto;
import io.github.spring.middleware.product.dto.ProductTypeDto;
import io.github.spring.middleware.review.domain.Review;
import io.github.spring.middleware.review.entity.ReviewEntity;
import io.github.spring.middleware.review.exceptions.ProductNotFoundException;
import io.github.spring.middleware.review.exceptions.ReviewNotFoundException;
import io.github.spring.middleware.review.mapper.ReviewMapper;
import io.github.spring.middleware.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.leangen.graphql.util.ClassFinder.log;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ProductApi productApi;

    @Override
    public Review createReview(Review review) {
        ProductDto productDto = productApi.getProduct(review.getProductId()); // Verificar que el producto existe antes de crear el review
        if (productDto == null) {
            throw new ProductNotFoundException(STR."Product not found for productId \{review.getProductId()}");
        }

        if (review.getId() == null) {
            review.setId(UUID.randomUUID());
        }
        ReviewEntity entity = reviewMapper.toEntity(review);
        Instant now = Instant.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        ReviewEntity saved = reviewRepository.save(entity);

        // Actualizar el producto con el nuevo reviewId
        updateProductReviewIds(review.getProductId(), productDto, saved.getId(), true);

        return reviewMapper.toDomain(saved);
    }

    @Override
    public List<Review> createReviewsForProduct(List<Review> reviews, UUID productId) {
        ProductDto productDto = productApi.getProduct(productId);
        if (productDto == null) {
            throw new ProductNotFoundException(STR."Product not found for productId \{productId}");
        }

        List<ReviewEntity> entities = reviews.stream()
                .map(review -> {
                    ReviewEntity entity = reviewMapper.toEntity(review);
                    entity.setProductId(productId);
                    if (entity.getId() == null) {
                        entity.setId(UUID.randomUUID());
                    }
                    Instant now = Instant.now();
                    entity.setCreatedAt(now);
                    entity.setUpdatedAt(now);
                    return entity;
                })
                .collect(Collectors.toList());
        List<ReviewEntity> saved = reviewRepository.saveAll(entities);

        // Actualizar el producto con los nuevos reviewIds
        List<UUID> newIds = saved.stream().map(ReviewEntity::getId).toList();

        updateProductReviewIds(productId, productDto, newIds, true);

        return saved.stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Review getReview(UUID id) {
        return reviewRepository.findById(id)
                .map(reviewMapper::toDomain)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    public Review replaceReview(UUID id, Review review) {
        if (!reviewRepository.existsById(id)) {
            throw new ReviewNotFoundException(id);
        }
        ReviewEntity entity = reviewMapper.toEntity(review);
        entity.setId(id);
        Instant now = Instant.now();
        entity.setCreatedAt(entity.getCreatedAt());
        entity.setUpdatedAt(now);
        ReviewEntity saved = reviewRepository.save(entity);
        return reviewMapper.toDomain(saved);
    }

    @Override
    public List<Review> getReviewsByIds(List<UUID> reviewIds) {
        return reviewRepository.findAllById(reviewIds).stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> replaceReviewsForProduct(List<Review> reviews, UUID productId) {
        List<ReviewEntity> entitiesToSave = reviews.stream()
                .map(review -> {
                    ReviewEntity entity = reviewMapper.toEntity(review);
                    entity.setProductId(productId);
                    if (entity.getId() == null) {
                        entity.setId(UUID.randomUUID());
                    }
                    Instant now = Instant.now();
                    entity.setCreatedAt(entity.getCreatedAt());
                    entity.setUpdatedAt(now);
                    return entity;
                })
                .collect(Collectors.toList());
        return reviewRepository.saveAll(entitiesToSave).stream()
                .map(reviewMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Review patchReview(UUID id, Review review) {
        ReviewEntity existing = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        if (review.getRating() != null) existing.setRating(review.getRating());
        if (review.getComment() != null) existing.setComment(review.getComment());
        if (review.getProductId() != null) existing.setProductId(review.getProductId());
        existing.setUpdatedAt(Instant.now());

        ReviewEntity saved = reviewRepository.save(existing);
        return reviewMapper.toDomain(saved);
    }

    @Override
    public Page<Review> listReviews(String q, UUID productId, Pageable pageable) {
        org.springframework.data.domain.Page<ReviewEntity> page;
        boolean hasQuery = q != null && !q.isBlank();
        boolean hasProductId = productId != null;

        if (hasQuery) {
            if (hasProductId) {
                page = reviewRepository.findByCommentContainingIgnoreCaseAndProductId(q, productId, pageable);
            } else {
                page = reviewRepository.findByCommentContainingIgnoreCase(q, pageable);
            }
        } else {
            if (hasProductId) {
                page = reviewRepository.findByProductId(productId, pageable);
            } else {
                page = reviewRepository.findAll(pageable);
            }
        }
        return page.map(reviewMapper::toDomain);
    }

    @Override
    public void deleteReview(UUID id) {
        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));

        reviewRepository.deleteById(id);

        ProductDto productDto = productApi.getProduct(entity.getProductId());

        // Eliminar el reviewId del producto
        updateProductReviewIds(entity.getProductId(), productDto, id, false);
    }

    @Override
    public void deleteReviewsFromProduct(List<UUID> ids, UUID productId) {
        ProductDto productDto = productApi.getProduct(productId);
        if (productDto == null) {
            throw new ProductNotFoundException(STR."Product not found for productId \{productId}");
        }

        List<ReviewEntity> entitiesToDelete = reviewRepository.findAllById(ids).stream()
                .filter(entity -> entity.getProductId().equals(productId))
                .collect(Collectors.toList());

        if (entitiesToDelete.size() != ids.size()) {
            throw new RuntimeException("Some reviews were not found for the given product");
        }

        reviewRepository.deleteAll(entitiesToDelete);


        // Eliminar los reviewIds del producto
        updateProductReviewIds(productId, productDto, ids, false);
    }

    private void updateProductReviewIds(UUID productId, ProductDto productDto, UUID reviewId, boolean add) {
        updateProductReviewIds(productId, productDto, List.of(reviewId), add);
    }

    private void updateProductReviewIds(UUID productId, ProductDto productDto, List<UUID> reviewIds, boolean add) {
        if (productId == null) return;
        try {
            log.info("{} reviewIds {} to product {}", add ? "Adding" : "Removing", reviewIds, productId);
            ProductPatchRequestDto productPatchRequestDto = null;
            ProductTypeDto productTypeDto = productDto.getProductType();
            if (productTypeDto == ProductTypeDto.DIGITAL) {
                DigitalProductDto digitalProductDto = (DigitalProductDto) productDto;
                if (add) {
                    digitalProductDto.getReviewIds().addAll(reviewIds);
                } else {
                    digitalProductDto.getReviewIds().removeAll(reviewIds);
                }
                DigitalProductPatchRequestDto digitalProductPatchRequestDto = new DigitalProductPatchRequestDto();
                digitalProductPatchRequestDto.setReviewIds(digitalProductDto.getReviewIds());
                productPatchRequestDto = digitalProductPatchRequestDto;
            } else if (productTypeDto == ProductTypeDto.PHYSICAL) {
                PhysicalProductDto physicalProductDto = (PhysicalProductDto) productDto;
                if (add) {
                    physicalProductDto.getReviewIds().addAll(reviewIds);
                } else {
                    physicalProductDto.getReviewIds().removeAll(reviewIds);
                }
                PhysicalProductPatchRequestDto physicalProductPatchRequestDto = new PhysicalProductPatchRequestDto();
                physicalProductPatchRequestDto.setReviewIds(reviewIds);
                productPatchRequestDto = physicalProductPatchRequestDto;
            }
            productApi.patchProduct(productId, productPatchRequestDto);
        } catch (Exception e) {
            log.error("Failed to update product reviewIds", e);
        }
    }
}
