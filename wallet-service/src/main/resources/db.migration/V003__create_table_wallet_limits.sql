CREATE TABLE wallet_limits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL,
    limit_type VARCHAR(255) NOT NULL,
    limit_amount NUMERIC(19,2) NOT NULL,
    used_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    reset_date TIMESTAMP,
    status VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_limit_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);
