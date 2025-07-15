CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    currency VARCHAR(20),
    description VARCHAR(255),
    status VARCHAR(20),
    amount DECIMAL(10, 2) NOT NULL,
    origin_bill VARCHAR(100) NOT NULL,
    destine_bill VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,

    -- Campos específicos para pix transaction
    key VARCHAR(100),

    -- Campos específicos para card transaction
    last_four_digits VARCHAR(4),
    flag VARCHAR(50),
    auth_code VARCHAR(100),
    installments INTEGER,
    card_type VARCHAR(20)
);
