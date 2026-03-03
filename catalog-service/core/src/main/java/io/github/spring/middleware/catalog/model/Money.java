package io.github.spring.middleware.catalog.model;

import lombok.Data;

@Data
public class Money {

    private String currency;
    private double amount;

}
