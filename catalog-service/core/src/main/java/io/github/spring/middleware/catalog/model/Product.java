package io.github.spring.middleware.catalog.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Product {

    private UUID id;
    private String name;
    private String sku;
    private ProductStatus productStatus;
    private Money price;
}
