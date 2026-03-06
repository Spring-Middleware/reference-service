package io.github.spring.middleware.product.dto.graphql;

import io.github.spring.middleware.product.domain.ProductStatus;

import java.util.UUID;

public class ProductInput {
    private UUID catalogId;
    private String sku;
    private String name;
    private String description;
    private ProductStatus status;
    private MoneyInput price;

    public UUID getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(UUID catalogId) {
        this.catalogId = catalogId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public MoneyInput getPrice() {
        return price;
    }

    public void setPrice(MoneyInput price) {
        this.price = price;
    }
}

