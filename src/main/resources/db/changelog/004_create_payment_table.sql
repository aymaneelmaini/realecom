CREATE SCHEMA IF NOT EXISTS payment_schema;

CREATE TYPE payment_schema.PAYMENT_STATUS AS ENUM ('PENDING', 'PROCESSING', 'SUCCESS', 'FAILED');

CREATE TABLE payment_schema.payments
(
    id                     UUID PRIMARY KEY,
    order_id               UUID           NOT NULL,
    payment_price_amount   DECIMAL(19, 3) NOT NULL,
    payment_price_currency VARCHAR(3)     NOT NULL DEFAULT 'USD',
    status                 payment_schema.PAYMENT_STATUS,
    external_payment_id    VARCHAR(255),
    created_at             TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT amount_positive CHECK (payments.payment_price_amount > 0)

);

CREATE INDEX idx_payments_order_id ON payment_schema.payments (order_id);
CREATE INDEX idx_payments_status ON payment_schema.payments (status);