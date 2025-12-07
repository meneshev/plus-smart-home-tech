INSERT INTO "storage".warehouse
(warehouse_name, Warehouse_begDt, warehouse_enddt)
VALUES('Склад по умолчанию', '2025-01-01 00:00:00.000', NULL);

INSERT INTO "storage".address
(warehouse_id, address_country, address_city, address_street, address_house, address_flat)
VALUES(1, 'Россия', 'Москва', 'ул. Лесная', '1', '1');
INSERT INTO "storage".address
(warehouse_id, address_country, address_city, address_street, address_house, address_flat)
VALUES(1, 'Россия', 'Москва', 'ул. Лесная', '1', '2');