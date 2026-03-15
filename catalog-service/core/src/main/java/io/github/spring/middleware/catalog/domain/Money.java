package io.github.spring.middleware.catalog.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Money {

    private String currency;
    private BigDecimal amount;

}
