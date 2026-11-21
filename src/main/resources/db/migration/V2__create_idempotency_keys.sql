CREATE TABLE idempotency_keys(
    idempotency_key varchar(100) NOT NULL,
    merchant_id varchar(100) NOT NULL,  --take it from JWT claim
    request_hash char(64) NOT NULL,
    status varchar(30) NOT NULL DEFAULT 'IN_PROGRESS'
        CHECK (status in ('IN_PROGRESS', 'COMPLETED')),
    response_code INT
        CHECK (status <> 'COMPLETED' or response_code IS NOT NULL),
    response_body jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    locked_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    PRIMARY KEY(idempotency_key, merchant_id)
);