package io.github.spring.middleware.catalog.entity;

import io.github.spring.middleware.catalog.domain.ProductType;

import java.util.UUID;


public record ProductIdWithType(UUID productId, ProductType productType) {
}
