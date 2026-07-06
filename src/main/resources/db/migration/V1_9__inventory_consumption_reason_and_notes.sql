SET @db = DATABASE();

SELECT COUNT(*) INTO @t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'inventory_entries';

SELECT COUNT(*) INTO @c1 FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'inventory_entries' AND COLUMN_NAME = 'consumption_reason';
SET @q = IF(@t > 0 AND @c1 = 0,
    'ALTER TABLE inventory_entries ADD COLUMN consumption_reason VARCHAR(50) NULL',
    'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @c2 FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'inventory_entries' AND COLUMN_NAME = 'usage_notes';
SET @q = IF(@t > 0 AND @c2 = 0,
    'ALTER TABLE inventory_entries ADD COLUMN usage_notes TEXT NULL',
    'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SET @q = IF(@t > 0,
    'UPDATE inventory_entries SET consumption_reason = COALESCE(NULLIF(final_product, ''''), ''other'') WHERE consumption_reason IS NULL',
    'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SET @q = IF(@t > 0,
    'ALTER TABLE inventory_entries MODIFY consumption_reason VARCHAR(50) NOT NULL',
    'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;
