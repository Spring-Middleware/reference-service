package io.github.spring.middleware.catalog.domain;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class CatalogWithProducts {

    private UUID id;
    private String name;
    private String description;
    private CatalogStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Product> products;

}
