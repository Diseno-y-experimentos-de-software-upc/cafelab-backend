SET @db = DATABASE();

SELECT COUNT(*) INTO @t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots';
SELECT COUNT(*) INTO @c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'original_weight';

SET @q = IF(@t > 0 AND @c = 0, 'ALTER TABLE coffee_lots ADD COLUMN original_weight DOUBLE', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

UPDATE coffee_lots
SET original_weight = weight
WHERE original_weight IS NULL;

SET @q = IF(@t > 0,
    'ALTER TABLE coffee_lots MODIFY original_weight DOUBLE NOT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;
