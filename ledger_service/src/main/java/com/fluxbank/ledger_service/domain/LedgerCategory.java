package com.fluxbank.ledger_service.domain;

public enum LedgerCategory {

    // owner-based (CUSTOMER / MERCHANT)
    CUSTOMER_DEPOSITS(AccountClass.LIABILITY),
    CREDIT_RECEIVABLE(AccountClass.ASSET),
    MERCHANT_PAYABLE(AccountClass.LIABILITY),

    // technical (INTERNAL)
    CASH_RESERVE(AccountClass.ASSET),
    SCHEME_SETTLEMENT(AccountClass.ASSET),
    ACQUIRER_RECEIVABLE(AccountClass.ASSET),
    SCHEME_SETTLEMENT_PAYABLE(AccountClass.LIABILITY),
    PIX_FEE_INCOME(AccountClass.REVENUE),
    TED_FEE_INCOME(AccountClass.REVENUE),
    MAINTENANCE_FEE_INCOME(AccountClass.REVENUE),
    CARD_INTERCHANGE_INCOME(AccountClass.REVENUE),
    ACQUIRING_MDR_INCOME(AccountClass.REVENUE),
    CREDIT_INTEREST_INCOME(AccountClass.REVENUE),
    CARD_INTERCHANGE_EXPENSE(AccountClass.EXPENSE);

    private final AccountClass accountClass;

    LedgerCategory(AccountClass accountClass) {
        this.accountClass = accountClass;
    }

    public AccountClass getAccountClass() {
        return accountClass;
    }
}