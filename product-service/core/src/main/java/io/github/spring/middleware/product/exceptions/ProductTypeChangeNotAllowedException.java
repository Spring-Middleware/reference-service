package io.github.spring.middleware.product.exceptions;


import io.github.spring.middleware.exception.ConflictException;

import static io.github.spring.middleware.product.error.ProductErrorCodes.PRODUCT_TYPE_CHANGE_NOT_ALLOWED;

public class ProductTypeChangeNotAllowedException extends ConflictException {


    public ProductTypeChangeNotAllowedException(String message) {
        super(PRODUCT_TYPE_CHANGE_NOT_ALLOWED, message);
    }

    public ProductTypeChangeNotAllowedException(String message, Throwable cause) {
        super(PRODUCT_TYPE_CHANGE_NOT_ALLOWED, message, cause);
    }
}
