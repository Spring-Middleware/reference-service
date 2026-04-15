package io.github.spring.middleware.review.exceptions;

import io.github.spring.middleware.error.ErrorCodes;

public enum ReviewErrorCodes implements ErrorCodes {

    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND");

    private String code;

    ReviewErrorCodes(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
