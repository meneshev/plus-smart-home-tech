--CREATE DATABASE commerce_warehouse;

CREATE SCHEMA IF NOT EXISTS storage;

-- Склад
CREATE TABLE IF NOT EXISTS storage.Warehouse (
     Warehouse_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     Warehouse_Name VARCHAR NOT NULL,
     Warehouse_begDt TIMESTAMP,
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

--Бронирование товаров
CREATE TABLE IF NOT EXISTS storage.Booking (
    Booking_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    Order_id UUID NOT NULL,
    Delivery_id UUID UNIQUE,
    Booking_state varchar
);

--Забронированные для заказа товары
CREATE TABLE IF NOT EXISTS storage.Booking_Products (
    Booking_id BIGINT REFERENCES storage.Booking(Booking_id),
    Product_id UUID NOT NULL,
    Warehouse_id BIGINT REFERENCES storage.Warehouse(Warehouse_id),
    Quantity BIGINT NOT NULL,
    PRIMARY KEY (Booking_id, Product_id, Warehouse_id)
);