package io.github.spring.middleware.review.domain;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Review {
    private UUID id;
    private UUID productId;
    private Integer rating;
    private String comments;
    private Instant createdAt;
    private Instant updatedAt;
}
