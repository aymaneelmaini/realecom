CREATE SCHEMA IF NOT EXISTS order_schema;

CREATE TYPE order_schema.ORDER_STATUS AS ENUM ('PENDING', 'RESERVED', 'PAID', 'CONFIRMED', 'FAILED', 'CANCELLED');

CREATE TABLE order_schema.orders
(
    id         UUID PRIMARY KEY,
    status     order_schema.ORDER_STATUS,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_schema.order_lines
(
    id             UUID PRIMARY KEY,
    order_id       UUID           NOT NULL REFERENCES order_schema.orders (id) ON DELETE CASCADE,
    product_id     UUID           NOT NULL,
    quantity       INT            NOT NULL,
    price_amount   DECIMAL(19, 3) NOT NULL,
    price_currency VARCHAR(3)     NOT NULL DEFAULT 'USD',
    CONSTRAINT quantity_positive CHECK ( quantity > 0 ),
    CONSTRAINT price_amount_positive CHECK ( price_amount > 0 )
);

CREATE INDEX idx_orders_status ON order_schema.orders (status);

CREATE INDEX idx_order_lines_product_id ON order_schema.order_lines (product_id);