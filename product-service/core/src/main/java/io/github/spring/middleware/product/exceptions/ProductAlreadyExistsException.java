package io.github.spring.middleware.product.exceptions;

import java.io.Serial;

public class ProductAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
