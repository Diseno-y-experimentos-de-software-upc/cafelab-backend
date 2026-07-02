ALTER TABLE inventory_entries
    ADD COLUMN IF NOT EXISTS consumption_reason VARCHAR(50) NULL,
    ADD COLUMN IF NOT EXISTS usage_notes TEXT NULL;

UPDATE inventory_entries
SET consumption_reason = COALESCE(NULLIF(final_product, ''), 'other')
WHERE consumption_reason IS NULL;

ALTER TABLE inventory_entries
    MODIFY COLUMN consumption_reason VARCHAR(50) NOT NULL;
