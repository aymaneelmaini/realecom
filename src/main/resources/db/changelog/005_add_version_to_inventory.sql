ALTER TABLE inventory_schema.inventories
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
