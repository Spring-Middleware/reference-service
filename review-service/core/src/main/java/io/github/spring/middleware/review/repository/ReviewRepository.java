package io.github.spring.middleware.review.repository;

import io.github.spring.middleware.review.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewEntity, UUID> {
    List<ReviewEntity> findByProductId(UUID productId);

    Page<ReviewEntity> findByProductId(UUID productId, Pageable pageable);

    Page<ReviewEntity> findByCommentsContainingIgnoreCase(String q, Pageable pageable);

    Page<ReviewEntity> findByCommentsContainingIgnoreCaseAndProductId(String q, UUID productId, Pageable pageable);
}
