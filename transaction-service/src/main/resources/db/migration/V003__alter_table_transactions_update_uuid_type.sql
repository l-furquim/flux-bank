ALTER TABLE transactions
    ALTER COLUMN payer_id TYPE uuid USING payer_id::uuid,
    ALTER COLUMN payee_id TYPE uuid USING payee_id::uuid;
