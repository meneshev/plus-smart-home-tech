--CREATE DATABASE commerce_warehouse;

CREATE SCHEMA IF NOT EXISTS storage;

-- Склад
CREATE TABLE IF NOT EXISTS storage.Warehouse (
     Warehouse_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     Warehouse_Name VARCHAR NOT NULL,
     Warehouse_bedDt TIMESTAMP,
     Warehouse_endDt TIMESTAMP
);

-- Адрес склада
CREATE TABLE IF NOT EXISTS storage.Address (
     Address_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     Warehouse_id BIGINT REFERENCES storage.Warehouse(Warehouse_id),
     Address_Country VARCHAR NOT NULL,
     Address_City VARCHAR NOT NULL,
     Address_Street VARCHAR NOT NULL,
     Address_House VARCHAR NOT NULL,
     Address_Flat VARCHAR
);

-- Характеристики товара
CREATE TABLE IF NOT EXISTS storage.ProductSpecs (
    Product_id UUID PRIMARY KEY,
    ProductSpecs_isFragile BOOL,
    ProductSpecs_Width DOUBLE PRECISION NOT NULL,
    ProductSpecs_Height DOUBLE PRECISION NOT NULL,
    ProductSpecs_Depth DOUBLE PRECISION NOT NULL,
    ProductSpecs_Weight DOUBLE PRECISION NOT NULL,
    ProductSpecs_Volume DOUBLE PRECISION GENERATED ALWAYS AS
        (ProductSpecs_Width * ProductSpecs_Height * ProductSpecs_Depth) STORED
);

-- Наличие на складе
CREATE TABLE IF NOT EXISTS storage.Warehouse_Product (
    Warehouse_id BIGINT REFERENCES storage.Warehouse(Warehouse_id),
    Product_id UUID NOT NULL,
    Quantity BIGINT NOT NULL,
    PRIMARY KEY (Warehouse_id, Product_id)
);

INSERT INTO "storage".warehouse
(warehouse_name, warehouse_beddt, warehouse_enddt)
VALUES('Склад по умолчанию', '2025-01-01 00:00:00.000', NULL);

INSERT INTO "storage".address
(warehouse_id, address_country, address_city, address_street, address_house, address_flat)
VALUES(1, 'Россия', 'Москва', 'ул. Лесная', '1', '1');
INSERT INTO "storage".address
(warehouse_id, address_country, address_city, address_street, address_house, address_flat)
VALUES(1, 'Россия', 'Москва', 'ул. Лесная', '1', '2');

