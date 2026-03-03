package io.github.spring.middleware.product.dto.graphql;

import io.github.spring.middleware.product.domain.ProductStatus;
import java.math.BigDecimal;

public class MoneyInput {
    private BigDecimal amount;
    private String currency;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

