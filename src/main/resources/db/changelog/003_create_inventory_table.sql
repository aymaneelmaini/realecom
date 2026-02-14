CREATE SCHEMA IF NOT EXISTS inventory_schema;

CREATE TABLE inventory_schema.inventories
(
    id                 UUID PRIMARY KEY,
    product_id         UUID NOT NULL UNIQUE,
    available_quantity INT  NOT NULL,
    reserved_quantity  INT  NOT NULL DEFAULT 0,
    CONSTRAINT available_quantity_non_negative CHECK (available_quantity >= 0),
    CONSTRAINT reserved_quantity_non_negative CHECK (reserved_quantity >= 0)
);

CREATE INDEX idx_inventories_product_id ON inventory_schema.inventories (product_id);
