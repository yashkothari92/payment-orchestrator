CREATE TABLE payments (
    id UUID PRIMARY KEY,
    amount NUMERIC(19,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    idempotency_key VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);