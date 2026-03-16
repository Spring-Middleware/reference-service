package io.github.spring.middleware.product.dto.graphql;

import lombok.Data;

@Data
public class PhysicalProductInput extends ProductInput {

    private DimensionsInput dimensions;
    private Integer stockQuantity;
    private Boolean shippable;
}
