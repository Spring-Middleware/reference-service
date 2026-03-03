package io.github.spring.middleware.catalog.domain;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class Catalog {

    private UUID id;
    private String name;
    private CatalogStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<Product> products;

}
