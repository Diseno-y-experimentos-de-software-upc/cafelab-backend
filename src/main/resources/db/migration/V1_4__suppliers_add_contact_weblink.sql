-- TUS01: ficha de proveedor ampliada (idempotente)

SET @db = DATABASE();

SELECT COUNT(*) INTO @t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'suppliers';
SELECT COUNT(*) INTO @c1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'suppliers' AND COLUMN_NAME = 'contact_person';
SELECT COUNT(*) INTO @c2 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'suppliers' AND COLUMN_NAME = 'web_link';

SET @q = IF(@t > 0 AND @c1 = 0, 'ALTER TABLE suppliers ADD COLUMN contact_person VARCHAR(100) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

SET @q = IF(@t > 0 AND @c2 = 0, 'ALTER TABLE suppliers ADD COLUMN web_link VARCHAR(200) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;
