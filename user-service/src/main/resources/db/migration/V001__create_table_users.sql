CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf VARCHAR(11) NOT NULL,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    birth_date DATE NOT NULL,
    address TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);
