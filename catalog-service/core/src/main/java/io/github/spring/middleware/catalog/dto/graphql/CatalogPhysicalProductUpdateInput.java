package io.github.spring.middleware.catalog.dto.graphql;

import io.github.spring.middleware.catalog.domain.Dimensions;
import io.github.spring.middleware.catalog.domain.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CatalogPhysicalProductUpdateInput {

    private UUID id;
    private String name;
    private String description;
    private String sku;
    private ProductStatus status;
    private BigDecimal priceAmount;
    private String priceCurrency;
    private Dimensions dimensions;
    private Integer stockQuantity;
    private Boolean shippable;

}
