package io.github.spring.middleware.review.exceptions;

import io.github.spring.middleware.error.ErrorDescriptor;
import io.github.spring.middleware.exception.NotFoundException;

public class ProductNotFoundException extends NotFoundException {

    public ProductNotFoundException(String message) {
        super(ErrorDescriptor.fromErrorCodes(ReviewErrorCodes.PRODUCT_NOT_FOUND), message);
    }
}
