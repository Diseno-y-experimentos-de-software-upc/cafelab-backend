-- TUS01: ficha de proveedor ampliada
-- Agrega persona de contacto y enlace web (ambos opcionales)
ALTER TABLE suppliers
    ADD COLUMN contact_person VARCHAR(100) NULL,
    ADD COLUMN web_link VARCHAR(200) NULL;
