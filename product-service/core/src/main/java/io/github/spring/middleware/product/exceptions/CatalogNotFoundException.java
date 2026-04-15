package io.github.spring.middleware.product.exceptions;

import io.github.spring.middleware.error.ErrorDescriptor;
import io.github.spring.middleware.exception.NotFoundException;
import io.github.spring.middleware.product.error.ProductErrorCodes;

public class CatalogNotFoundException extends NotFoundException {


    public CatalogNotFoundException(String message) {
        super(ErrorDescriptor.fromErrorCodes(ProductErrorCodes.CATALOG_NOT_FOUND), message);
    }

    public CatalogNotFoundException(ErrorDescriptor descriptor, String message, Throwable cause) {
        super(ErrorDescriptor.fromErrorCodes(ProductErrorCodes.CATALOG_NOT_FOUND), message, cause);
    }
}
