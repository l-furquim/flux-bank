ALTER TABLE entry_accounts
DROP CONSTRAINT chk_entry_accounts_category;

ALTER TABLE entry_accounts
    ADD CONSTRAINT chk_entry_accounts_category
        CHECK (category IN (
                            'CUSTOMER_DEPOSITS',
                            'CREDIT_RECEIVABLE',
                            'MERCHANT_PAYABLE',
                            'CASH_RESERVE',
                            'SCHEME_SETTLEMENT',
                            'ACQUIRER_RECEIVABLE',
                            'PIX_FEE_INCOME',
                            'TED_FEE_INCOME',
                            'MAINTENANCE_FEE_INCOME',
                            'CARD_INTERCHANGE_INCOME',
                            'CARD_INTERCHANGE_EXPENSE',
                            'ACQUIRING_MDR_INCOME',
                            'CREDIT_INTEREST_INCOME',
                            'SCHEME_SETTLEMENT_PAYABLE'
            ));
INSERT INTO entry_accounts (owner_type, account_class, code, category, currency)
VALUES
    ('INTERNAL', 'ASSET', 'ASSET.CASH_BACEN',            'CASH_RESERVE',         'BRL'),
    ('INTERNAL', 'ASSET', 'ASSET.SCHEME_SETTLEMENT',      'SCHEME_SETTLEMENT',    'BRL'),
    ('INTERNAL', 'ASSET', 'ASSET.ACQUIRER_RECEIVABLE',    'ACQUIRER_RECEIVABLE',  'BRL');

INSERT INTO entry_accounts (owner_type, account_class, code, category, currency)
VALUES
    ('INTERNAL', 'LIABILITY', 'LIABILITY.SCHEME_SETTLEMENT_PAYABLE', 'SCHEME_SETTLEMENT_PAYABLE', 'BRL');

INSERT INTO entry_accounts (owner_type, account_class, code, category, currency)
VALUES
    ('INTERNAL', 'REVENUE', 'REVENUE.PIX_FEE_INCOME',         'PIX_FEE_INCOME',           'BRL'),
    ('INTERNAL', 'REVENUE', 'REVENUE.TED_FEE_INCOME',         'TED_FEE_INCOME',           'BRL'),
    ('INTERNAL', 'REVENUE', 'REVENUE.MAINTENANCE_FEE_INCOME', 'MAINTENANCE_FEE_INCOME',   'BRL'),
    ('INTERNAL', 'REVENUE', 'REVENUE.CARD_INTERCHANGE_INCOME','CARD_INTERCHANGE_INCOME',  'BRL'),
    ('INTERNAL', 'REVENUE', 'REVENUE.ACQUIRING_MDR_INCOME',   'ACQUIRING_MDR_INCOME',     'BRL'),
    ('INTERNAL', 'REVENUE', 'REVENUE.CREDIT_INTEREST_INCOME', 'CREDIT_INTEREST_INCOME',   'BRL');

INSERT INTO entry_accounts (owner_type, account_class, code, category, currency)
VALUES
    ('INTERNAL', 'EXPENSE', 'EXPENSE.CARD_INTERCHANGE_EXPENSE', 'CARD_INTERCHANGE_EXPENSE', 'BRL');