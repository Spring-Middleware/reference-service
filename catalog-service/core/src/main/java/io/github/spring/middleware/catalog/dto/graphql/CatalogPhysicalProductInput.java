package io.github.spring.middleware.catalog.dto.graphql;

import io.github.spring.middleware.catalog.domain.Dimensions;
import io.github.spring.middleware.catalog.domain.ProductStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CatalogPhysicalProductInput {

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
