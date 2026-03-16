package io.github.spring.middleware.product.entity;

import io.github.spring.middleware.product.domain.Dimensions;
import lombok.Data;

@Data
public class PhysicalProductEntity extends BaseProductEntity {

    private Dimensions dimensions;
    private Integer stockQuantity;
    private Boolean shippable;
}

