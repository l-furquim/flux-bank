package com.fluxbank.wallet_service.domain.enums;

import com.fluxbank.wallet_service.domain.exception.UnsupportedCurrencyException;

public enum Currency {
    BRL("BRL"), USD("USD"), EUR("EUR");

    private String value;

    Currency(String value) {
        this.value = value;
    }

    public static Currency fromValue(String value) {
        for (Currency c : Currency.values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new UnsupportedCurrencyException();
    }


}
