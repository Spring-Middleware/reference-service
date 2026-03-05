package io.github.spring.middleware.catalog.exception;

import java.io.Serial;

public class CatalogNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CatalogNotFoundException(String message) {
        super(message);
    }
}
