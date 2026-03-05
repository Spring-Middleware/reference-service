package io.github.spring.middleware.product.exceptions;

import io.github.spring.middleware.exception.NotFoundException;
import io.github.spring.middleware.product.error.ProductErrorCodes;

import java.io.Serial;

public class ProductNotFoundException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProductNotFoundException(String message) {
        super(ProductErrorCodes.PRODUCT_NOT_FOUND, message);
    }
}
