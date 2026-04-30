package io.github.spring.middleware.product.domain;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class Review {
    private UUID id;
    private Integer rating;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
}
