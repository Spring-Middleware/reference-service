package io.github.spring.middleware.catalog.dto.graphql;

import io.github.spring.middleware.catalog.domain.CatalogStatus;

public class CatalogInput {

    private String name;
    private CatalogStatus status;

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
}

