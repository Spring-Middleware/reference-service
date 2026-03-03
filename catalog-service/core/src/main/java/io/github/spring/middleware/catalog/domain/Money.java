package io.github.spring.middleware.catalog.domain;

import lombok.Data;

@Data
public class Money {

    private String currency;
    private double amount;

}
