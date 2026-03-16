package io.github.spring.middleware.product.domain;

import lombok.Data;

import java.net.URI;

@Data
public class DigitalProduct extends Product {

    private Long fileSize;
    private String fileFormat;
    private URI downloadUrl;

    public ProductType getProductType() {
        return ProductType.DIGITAL;
    }
}
