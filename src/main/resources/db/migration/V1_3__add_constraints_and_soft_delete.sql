-- Soft delete: columna deleted_at en todas las tablas principales
-- (Hibernate ya la agrega vía ddl-auto=update; este script garantiza que exista en BD)
ALTER TABLE coffee_lots    ADD COLUMN IF NOT EXISTS deleted_at DATETIME(6) NULL;
ALTER TABLE suppliers      ADD COLUMN IF NOT EXISTS deleted_at DATETIME(6) NULL;
ALTER TABLE recipes        ADD COLUMN IF NOT EXISTS deleted_at DATETIME(6) NULL;
ALTER TABLE defects        ADD COLUMN IF NOT EXISTS deleted_at DATETIME(6) NULL;
ALTER TABLE inventory_entries   ADD COLUMN IF NOT EXISTS deleted_at DATETIME(6) NULL;
ALTER TABLE grind_calibrations  ADD COLUMN IF NOT EXISTS deleted_at DATETIME(6) NULL;
ALTER TABLE cupping_sessions    ADD COLUMN IF NOT EXISTS deleted_at DATETIME(6) NULL;

-- Índices únicos compuestos para evitar duplicados por usuario
ALTER TABLE coffee_lots ADD CONSTRAINT uq_coffee_lot_name_user
    UNIQUE (lot_name, user_id);

ALTER TABLE suppliers ADD CONSTRAINT uq_supplier_name_user
    UNIQUE (name, user_id);

ALTER TABLE recipes ADD CONSTRAINT uq_recipe_name_user
    UNIQUE (name, user_id);

-- CHECK constraints para validaciones de negocio en BD
ALTER TABLE coffee_lots ADD CONSTRAINT chk_lot_status
    CHECK (status IN ('green', 'roasted'));

ALTER TABLE coffee_lots ADD CONSTRAINT chk_lot_altitude
    CHECK (altitude >= 0 AND altitude <= 2500);

ALTER TABLE coffee_lots ADD CONSTRAINT chk_lot_weight
    CHECK (weight >= 1.0 AND weight <= 70.0);

ALTER TABLE defects ADD CONSTRAINT chk_defect_percentage
    CHECK (percentage >= 0.0 AND percentage <= 100.0);

ALTER TABLE defects ADD CONSTRAINT chk_defect_weight
    CHECK (defect_weight >= 0.0);
