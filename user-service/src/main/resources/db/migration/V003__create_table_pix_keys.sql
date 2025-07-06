CREATE TABLE pix_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    issued_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    value TEXT NOT NULL,
    CONSTRAINT fk_pixkey_user FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);
