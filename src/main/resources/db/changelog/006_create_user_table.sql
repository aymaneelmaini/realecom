CREATE SCHEMA IF NOT EXISTS user_schema;

CREATE TYPE user_schema.ROLE AS ENUM ('ADMIN', 'MANAGER', 'CUSTOMER');

CREATE TABLE user_schema.users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    role       VARCHAR(50)  NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON user_schema.users (email);
