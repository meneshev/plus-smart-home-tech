--CREATE DATABASE commerce_shopping_store;

--CREATE USER "commerce.store" WITH PASSWORD '12345';

CREATE SCHEMA IF NOT EXISTS store;

--GRANT ALL PRIVILEGES ON DATABASE commerce_shopping_store TO "commerce.store";

CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    image_src VARCHAR,
    quantity_state VARCHAR NOT NULL,
    product_state VARCHAR NOT NULL,
    product_category VARCHAR,
    price DOUBLE PRECISION NOT NULL
);