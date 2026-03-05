package io.github.spring.middleware.product.error;

import io.github.spring.middleware.error.ErrorDescriptor;

public enum ProductErrorCodes implements ErrorDescriptor {

    PRODUCT_NOT_FOUND("PRODUCT:NOT_FOUND"),
    PRODUCT_ALREADY_EXISTS("PRODUCT:ALREADY_EXISTS"),
    INVALID_PRODUCT_DATA("PRODUCT:INVALID_DATA");


    private final String code;

    ProductErrorCodes(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
