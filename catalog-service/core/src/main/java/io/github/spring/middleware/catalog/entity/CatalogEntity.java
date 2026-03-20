package io.github.spring.middleware.catalog.entity;

import io.github.spring.middleware.catalog.domain.CatalogStatus;
import io.github.spring.middleware.catalog.domain.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "catalogs")
public class CatalogEntity {

    @Id
    private UUID id;

    private String name;

    private String description;

    private CatalogStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private List<ProductIdWithType> productIdWithTypes = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CatalogStatus getStatus() {
        return status;
    }

    public void setStatus(CatalogStatus status) {
        this.status = status;
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

    public void addProductId(UUID productId, ProductType productType) {
        if (this.productIdWithTypes == null) {
            this.productIdWithTypes = new ArrayList<>();
        }
        ProductIdWithType productIdWithType = new ProductIdWithType(productId, productType);
        this.productIdWithTypes.add(productIdWithType);
    }

    public void removeProductId(UUID productId) {
        if (productId == null) {
            return;
        }
        this.productIdWithTypes.removeIf(p -> p.productId().equals(productId));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogEntity that = (CatalogEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return STR."CatalogEntity{id='\{id}', name='\{name}', status=\{status}, productIdWithType=\{productIdWithTypes}, createdAt=\{createdAt}, updatedAt=\{updatedAt}}";
    }
}

