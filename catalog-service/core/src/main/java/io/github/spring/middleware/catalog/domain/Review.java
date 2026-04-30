package io.github.spring.middleware.catalog.domain;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class Review {
    private UUID id;
    private UUID productId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
    private Instant updatedAt;
}

