SET @db = DATABASE();

SELECT COUNT(*) INTO @t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots';

SELECT COUNT(*) INTO @c1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'record_status';
SET @q = IF(@t > 0 AND @c1 = 0, 'ALTER TABLE coffee_lots ADD COLUMN record_status VARCHAR(20) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SELECT COUNT(*) INTO @c2 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'annulment_reason';
SET @q = IF(@t > 0 AND @c2 = 0, 'ALTER TABLE coffee_lots ADD COLUMN annulment_reason VARCHAR(25) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

UPDATE coffee_lots SET record_status = 'activo' WHERE record_status IS NULL;
UPDATE coffee_lots SET annulment_reason = '' WHERE annulment_reason IS NULL;

SET @q = IF(@t > 0, 'ALTER TABLE coffee_lots MODIFY record_status VARCHAR(20) NOT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SET @q = IF(@t > 0, 'ALTER TABLE coffee_lots MODIFY annulment_reason VARCHAR(25) NOT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;
