package io.github.spring.middleware.catalog.domain;

import io.leangen.graphql.annotations.types.GraphQLUnion;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@GraphQLUnion(name = "Product", possibleTypes = {PhysicalProduct.class, DigitalProduct.class})
public abstract class Product {

    private UUID id;
    private String name;
    private String description;
    private String sku;
    private ProductStatus status;
    private Money price;
    private Instant createdAt;
    private Instant updatedAt;
    private List<UUID> reviewIds;

    public abstract ProductType getProductType();

}
