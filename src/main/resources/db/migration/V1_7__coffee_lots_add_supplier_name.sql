SET @db = DATABASE();

SELECT COUNT(*) INTO @t FROM information_schema.TABLES WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots';
SELECT COUNT(*) INTO @c FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'coffee_lots' AND COLUMN_NAME = 'supplier_name';

SET @q = IF(@t > 0 AND @c = 0, 'ALTER TABLE coffee_lots ADD COLUMN supplier_name VARCHAR(100) NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

UPDATE coffee_lots cl
INNER JOIN suppliers s ON s.id = cl.supplier_id
SET cl.supplier_name = s.name
WHERE cl.supplier_name IS NULL;

UPDATE coffee_lots
SET supplier_name = 'Proveedor no disponible'
WHERE supplier_name IS NULL OR TRIM(supplier_name) = '';

SET @q = IF(@t > 0, 'ALTER TABLE coffee_lots MODIFY supplier_name VARCHAR(100) NOT NULL', 'SELECT 1');
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;
