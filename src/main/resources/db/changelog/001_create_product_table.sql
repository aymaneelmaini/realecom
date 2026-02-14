CREATE SCHEMA IF NOT EXISTS product_schema;

CREATE TABLE product_schema.products
(
    id                     UUID PRIMARY KEY,
    name                   VARCHAR(255)   NOT NULL,
    description            TEXT,
    product_price_amount   DECIMAL(19, 3) NOT NULL,
    product_price_currency VARCHAR(3)     NOT NULL DEFAULT 'USD',
    created_at             TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT price_amount_positive CHECK (product_price_amount > 0)
);