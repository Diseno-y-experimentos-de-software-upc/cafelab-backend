-- Soft delete + constraints. Idempotente (sin IF NOT EXISTS, compatible MySQL < 8.0.29).

SET @db = DATABASE();

-- deleted_at en tablas principales
SELECT COUNT(*) INTO @coffee_lots_t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots';
SELECT COUNT(*) INTO @coffee_lots_c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'deleted_at';
SET @q = IF(@coffee_lots_t > 0 AND @coffee_lots_c = 0, 'ALTER TABLE coffee_lots ADD COLUMN deleted_at DATETIME(6) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @suppliers_t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'suppliers';
SELECT COUNT(*) INTO @suppliers_c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'suppliers' AND COLUMN_NAME = 'deleted_at';
SET @q = IF(@suppliers_t > 0 AND @suppliers_c = 0, 'ALTER TABLE suppliers ADD COLUMN deleted_at DATETIME(6) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @recipes_t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'recipes';
SELECT COUNT(*) INTO @recipes_c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'recipes' AND COLUMN_NAME = 'deleted_at';
SET @q = IF(@recipes_t > 0 AND @recipes_c = 0, 'ALTER TABLE recipes ADD COLUMN deleted_at DATETIME(6) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @defects_t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'defects';
SELECT COUNT(*) INTO @defects_c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'defects' AND COLUMN_NAME = 'deleted_at';
SET @q = IF(@defects_t > 0 AND @defects_c = 0, 'ALTER TABLE defects ADD COLUMN deleted_at DATETIME(6) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @inv_t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'inventory_entries';
SELECT COUNT(*) INTO @inv_c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'inventory_entries' AND COLUMN_NAME = 'deleted_at';
SET @q = IF(@inv_t > 0 AND @inv_c = 0, 'ALTER TABLE inventory_entries ADD COLUMN deleted_at DATETIME(6) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @grind_t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'grind_calibrations';
SELECT COUNT(*) INTO @grind_c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'grind_calibrations' AND COLUMN_NAME = 'deleted_at';
SET @q = IF(@grind_t > 0 AND @grind_c = 0, 'ALTER TABLE grind_calibrations ADD COLUMN deleted_at DATETIME(6) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @cup_t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'cupping_sessions';
SELECT COUNT(*) INTO @cup_c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'cupping_sessions' AND COLUMN_NAME = 'deleted_at';
SET @q = IF(@cup_t > 0 AND @cup_c = 0, 'ALTER TABLE cupping_sessions ADD COLUMN deleted_at DATETIME(6) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Índices únicos compuestos
SELECT COUNT(*) INTO @uq_lot FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND CONSTRAINT_NAME = 'uq_coffee_lot_name_user';
SET @q = IF(@coffee_lots_t > 0 AND @uq_lot = 0,
    'ALTER TABLE coffee_lots ADD CONSTRAINT uq_coffee_lot_name_user UNIQUE (lot_name, user_id)', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @uq_sup FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'suppliers' AND CONSTRAINT_NAME = 'uq_supplier_name_user';
SET @q = IF(@suppliers_t > 0 AND @uq_sup = 0,
    'ALTER TABLE suppliers ADD CONSTRAINT uq_supplier_name_user UNIQUE (name, user_id)', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @uq_rec FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'recipes' AND CONSTRAINT_NAME = 'uq_recipe_name_user';
SET @q = IF(@recipes_t > 0 AND @uq_rec = 0,
    'ALTER TABLE recipes ADD CONSTRAINT uq_recipe_name_user UNIQUE (name, user_id)', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- CHECK constraints
SELECT COUNT(*) INTO @chk_status FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND CONSTRAINT_NAME = 'chk_lot_status';
SET @q = IF(@coffee_lots_t > 0 AND @chk_status = 0,
    'ALTER TABLE coffee_lots ADD CONSTRAINT chk_lot_status CHECK (status IN (''green'', ''roasted''))', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @chk_alt FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND CONSTRAINT_NAME = 'chk_lot_altitude';
SET @q = IF(@coffee_lots_t > 0 AND @chk_alt = 0,
    'ALTER TABLE coffee_lots ADD CONSTRAINT chk_lot_altitude CHECK (altitude >= 0 AND altitude <= 2500)', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @chk_weight FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND CONSTRAINT_NAME = 'chk_lot_weight';
SET @q = IF(@coffee_lots_t > 0 AND @chk_weight = 0,
    'ALTER TABLE coffee_lots ADD CONSTRAINT chk_lot_weight CHECK (weight >= 1.0 AND weight <= 70.0)', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @chk_pct FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'defects' AND CONSTRAINT_NAME = 'chk_defect_percentage';
SET @q = IF(@defects_t > 0 AND @chk_pct = 0,
    'ALTER TABLE defects ADD CONSTRAINT chk_defect_percentage CHECK (percentage >= 0.0 AND percentage <= 100.0)', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @chk_dw FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'defects' AND CONSTRAINT_NAME = 'chk_defect_weight';
SET @q = IF(@defects_t > 0 AND @chk_dw = 0,
    'ALTER TABLE defects ADD CONSTRAINT chk_defect_weight CHECK (defect_weight >= 0.0)', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;
