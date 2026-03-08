package io.github.spring.middleware.catalog.exception;

import io.github.spring.middleware.exception.NotFoundException;

import java.io.Serial;

public class CatalogNotFoundException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CatalogNotFoundException(CatalogErrorCodes errorCodes, String message) {
        super(errorCodes, message);
    }
}
