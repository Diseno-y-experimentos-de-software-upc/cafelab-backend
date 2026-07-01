ALTER TABLE inventory_entries
    ADD COLUMN IF NOT EXISTS motivo_de_consumo VARCHAR(50) NULL,
    ADD COLUMN IF NOT EXISTS notas_de_uso TEXT NULL;

UPDATE inventory_entries
SET motivo_de_consumo = COALESCE(NULLIF(final_product, ''), 'otro')
WHERE motivo_de_consumo IS NULL;

ALTER TABLE inventory_entries
    MODIFY COLUMN motivo_de_consumo VARCHAR(50) NOT NULL;
