package io.github.spring.middleware.product.domain;

import lombok.Data;

@Data
public class PhysicalProductWithReviews extends ProductWithReviews {

    private Dimensions dimensions;
    private Integer stockQuantity;
    private Boolean shippable;

    @Override
    public ProductType getProductType() {
        return ProductType.PHYSICAL;
    }
}
