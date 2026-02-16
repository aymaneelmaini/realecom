CREATE SCHEMA IF NOT EXISTS order_schema;

CREATE TABLE order_schema.orders
(
    id         UUID PRIMARY KEY,
    status     VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_order_status CHECK (status IN ('PENDING', 'RESERVED', 'PAID', 'CONFIRMED', 'FAILED', 'CANCELLED'))
);

CREATE TABLE order_schema.order_lines
(
    id             UUID PRIMARY KEY,
    order_id       UUID           NOT NULL REFERENCES order_schema.orders (id) ON DELETE CASCADE,
    product_id     UUID           NOT NULL,
    quantity       INT            NOT NULL,
    order_line_price_amount   DECIMAL(19, 3) NOT NULL,
    order_line_price_currency VARCHAR(3)     NOT NULL DEFAULT 'USD',
    CONSTRAINT quantity_positive CHECK ( quantity > 0 ),
    CONSTRAINT price_amount_positive CHECK ( order_line_price_amount > 0 )
);

CREATE INDEX idx_orders_status ON order_schema.orders (status);

CREATE INDEX idx_order_lines_product_id ON order_schema.order_lines (product_id);