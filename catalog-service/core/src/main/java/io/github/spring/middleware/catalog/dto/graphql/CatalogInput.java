package io.github.spring.middleware.catalog.dto.graphql;

import io.github.spring.middleware.catalog.domain.CatalogStatus;
import lombok.Data;

@Data
public class CatalogInput {

    private String name;
    private String description;
    private CatalogStatus status;

}

