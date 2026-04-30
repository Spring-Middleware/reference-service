package io.github.spring.middleware.product.domain;

import lombok.Data;

@Data
public class DigitalProductWithReviews extends ProductWithReviews {

    private String fileFormat;
    private Long fileSize;
    private Boolean downloadable;

    @Override
    public ProductType getProductType() {
        return ProductType.DIGITAL;
    }
}
