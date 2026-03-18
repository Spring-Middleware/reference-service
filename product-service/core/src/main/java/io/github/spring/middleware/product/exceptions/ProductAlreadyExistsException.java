package io.github.spring.middleware.product.exceptions;

import io.github.spring.middleware.exception.ConflictException;
import io.github.spring.middleware.product.error.ProductErrorCodes;

import java.io.Serial;

public class ProductAlreadyExistsException extends ConflictException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductAlreadyExistsException(String message) {
        super(ProductErrorCodes.PRODUCT_ALREADY_EXISTS, message);
    }
}
