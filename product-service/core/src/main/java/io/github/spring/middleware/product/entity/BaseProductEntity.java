package io.github.spring.middleware.product.entity;

import io.github.spring.middleware.product.domain.Money;
import io.github.spring.middleware.product.domain.ProductStatus;
import io.github.spring.middleware.product.domain.ProductType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "products")
public abstract class BaseProductEntity {

    @Id
    private UUID id;

    @Indexed
    private UUID catalogId;

    @Indexed(unique = true)
    private String sku;

    private String name;

    private String description;

    private ProductStatus status;

    private Money price;

    private ProductType productType;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private List<UUID> reviewIds;
}
