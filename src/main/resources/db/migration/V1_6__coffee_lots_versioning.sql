SET @db = DATABASE();

SELECT COUNT(*) INTO @t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots';

SELECT COUNT(*) INTO @c1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'lot_lineage_id';
SET @q = IF(@t > 0 AND @c1 = 0, 'ALTER TABLE coffee_lots ADD COLUMN lot_lineage_id BIGINT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @c2 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'version_number';
SET @q = IF(@t > 0 AND @c2 = 0, 'ALTER TABLE coffee_lots ADD COLUMN version_number INT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @c3 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'is_current';
SET @q = IF(@t > 0 AND @c3 = 0, 'ALTER TABLE coffee_lots ADD COLUMN is_current BOOLEAN NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @c4 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'supersedes_id';
SET @q = IF(@t > 0 AND @c4 = 0, 'ALTER TABLE coffee_lots ADD COLUMN supersedes_id BIGINT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

UPDATE coffee_lots SET lot_lineage_id = id WHERE lot_lineage_id IS NULL;
UPDATE coffee_lots SET version_number = 1 WHERE version_number IS NULL;
UPDATE coffee_lots SET is_current = TRUE WHERE is_current IS NULL;

SET @q = IF(@t > 0, 'ALTER TABLE coffee_lots MODIFY lot_lineage_id BIGINT NOT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SET @q = IF(@t > 0, 'ALTER TABLE coffee_lots MODIFY version_number INT NOT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SET @q = IF(@t > 0, 'ALTER TABLE coffee_lots MODIFY is_current BOOLEAN NOT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @old_uq FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND INDEX_NAME = 'uq_coffee_lot_name_user';
SET @q = IF(@t > 0 AND @old_uq > 0, 'ALTER TABLE coffee_lots DROP INDEX uq_coffee_lot_name_user', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @new_uq FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND CONSTRAINT_NAME = 'uq_coffee_lot_lineage_version';
SET @q = IF(@t > 0 AND @new_uq = 0,
    'ALTER TABLE coffee_lots ADD CONSTRAINT uq_coffee_lot_lineage_version UNIQUE (lot_lineage_id, version_number)', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;
