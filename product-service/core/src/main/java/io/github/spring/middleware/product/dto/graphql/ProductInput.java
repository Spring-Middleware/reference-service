package io.github.spring.middleware.product.dto.graphql;

import io.github.spring.middleware.product.domain.ProductStatus;
import io.github.spring.middleware.product.domain.ProductType;
import io.leangen.graphql.annotations.types.GraphQLUnion;
import lombok.Data;

import java.util.UUID;


@Data
public abstract class ProductInput {
    private UUID catalogId;
    private String sku;
    private String name;
    private String description;
    private ProductStatus status;
    private MoneyInput price;
    private ProductType productType;
}
