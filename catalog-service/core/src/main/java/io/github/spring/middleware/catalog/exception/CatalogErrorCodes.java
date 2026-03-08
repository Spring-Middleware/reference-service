package io.github.spring.middleware.catalog.exception;

import io.github.spring.middleware.error.ErrorDescriptor;

public enum CatalogErrorCodes implements ErrorDescriptor {

    CATALOG_NOT_FOUND("CATALOG_NOT_FOUND"),
    CATALOG_ALREADY_EXISTS("CATALOG_ALREADY_EXISTS");

    private final String code;

    CatalogErrorCodes(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
