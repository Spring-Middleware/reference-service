package io.github.spring.middleware.review.dto.graphql;

import lombok.Data;
import java.util.UUID;

@Data
public class ReviewInput {
    private UUID productId;
    private Integer rating;
    private String comments;
}

