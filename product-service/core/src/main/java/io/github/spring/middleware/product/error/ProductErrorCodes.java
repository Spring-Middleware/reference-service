package io.github.spring.middleware.product.error;

import io.github.spring.middleware.error.ErrorDescriptor;

public enum ProductErrorCodes implements ErrorDescriptor {

    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND"),
    PRODUCT_ALREADY_EXISTS("PRODUCT_ALREADY_EXISTS"),
    CATALOG_NOT_FOUND("CATALOG_NOT_FOUND"),
    INVALID_PRODUCT_DATA("PRODUCT_INVALID_DATA"),
    PRODUCT_TYPE_CHANGE_NOT_ALLOWED("PRODUCT_TYPE_CHANGE_NOT_ALLOWED");


    private final String code;

    ProductErrorCodes(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
