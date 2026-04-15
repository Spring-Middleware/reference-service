package io.github.spring.middleware.product.domain;

import io.github.spring.middleware.annotation.graphql.GraphQLLink;
import io.github.spring.middleware.annotation.graphql.GraphQLLinkArgument;
import io.github.spring.middleware.annotation.graphql.GraphQLLinkClass;
import io.github.spring.middleware.annotation.graphql.GraphQLType;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.types.GraphQLUnion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@GraphQLUnion(name = "Product", possibleTypes = {PhysicalProduct.class, DigitalProduct.class})
@GraphQLLinkClass(types = {@GraphQLType(names = "Product"), @GraphQLType(names = "Page_Product", isWrapper = true)})
public abstract class Product {

    private UUID id;
    @GraphQLLink(schema = "catalog", type = "Catalog", query = "catalog", arguments = {@GraphQLLinkArgument(name = "id")})
    private UUID catalogId;
    private String sku;
    private String name;
    private String description;
    private ProductStatus status;
    private Money price;
    private Instant createdAt;
    private Instant updatedAt;
    @GraphQLLink(schema = "review", type = "Review", query = "reviewsByIds", arguments = {
            @GraphQLLinkArgument(name = "ids", targetFieldName = "id", batch = true)}, batched = true, collection = true)
    private List<UUID> reviewIds;

    public Product() {
    }

    public Product(UUID id, UUID catalogId, String sku, String name, String description, ProductStatus status, Money price, Instant createdAt, Instant updatedAt, List<UUID> reviewIds) {
        this.id = id;
        this.catalogId = catalogId;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.status = status;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewIds = reviewIds;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @GraphQLQuery(name = "catalog")
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

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @GraphQLQuery(name = "reviews")
    public List<UUID> getReviewIds() {
        return reviewIds;
    }

    public void setReviewIds(List<UUID> reviewIds) {
        this.reviewIds = reviewIds;
    }

    public abstract ProductType getProductType();
}
