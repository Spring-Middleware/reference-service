package io.github.spring.middleware.catalog.domain;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public abstract class ProductWithReviews {

    private UUID id;
    private String name;
    private String description;
    private String sku;
    private ProductStatus status;
    private Money price;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Review> reviews;

    public abstract ProductType getProductType();

}
