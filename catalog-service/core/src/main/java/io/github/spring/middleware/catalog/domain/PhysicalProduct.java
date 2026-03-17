package io.github.spring.middleware.catalog.domain;

import lombok.Data;

@Data
public class PhysicalProduct extends Product {

    private Dimensions dimensions;
    private Integer stockQuantity;
    private Boolean shippable;

    @Override
    public ProductType getProductType() {
        return ProductType.PHYSICAL;
    }
}
