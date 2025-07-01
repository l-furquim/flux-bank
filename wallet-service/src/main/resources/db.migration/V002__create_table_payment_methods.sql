CREATE TABLE payment_methods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL,
    method_type VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    last_four_digits VARCHAR(255),
    expiry_date TIMESTAMP,
    status VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE,
    metadata TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);
