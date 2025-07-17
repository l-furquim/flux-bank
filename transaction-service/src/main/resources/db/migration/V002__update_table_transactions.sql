ALTER TABLE transactions
    RENAME COLUMN origin_bill TO payer_id;

ALTER TABLE transactions
    RENAME COLUMN destine_bill TO payee_id;