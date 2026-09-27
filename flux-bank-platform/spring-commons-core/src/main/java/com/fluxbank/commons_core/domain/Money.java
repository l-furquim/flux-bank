package com.fluxbank.commons_core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class Money {

    private BigDecimal amount;
    private Currency currency;

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount, Currency.BRL);
    }

    public void add(Money money) {
        this.amount = this.amount.add(money.amount);
    }

    public void subtract(Money money) {
        this.amount = this.amount.subtract(money.amount);
    }

}
