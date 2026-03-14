package io.github.spring.middleware.catalog.dto.graphql;

import io.github.spring.middleware.catalog.domain.ProductStatus;
import io.github.spring.middleware.catalog.domain.Money;

import java.math.BigDecimal;

public class CatalogProductInput {

    private String name;
    private String description;
    private String sku;
    private ProductStatus status;
    private BigDecimal priceAmount;
    private String priceCurrency;

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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }
}

