-- Idempotente: compatible con esquemas creados por Hibernate (sin coffee_id en defects)
-- y con BDs legacy que aún conservan esa columna. V1_2 elimina coffee_id si existía.

SET @db = DATABASE();

-- defects.coffee_id → coffees (solo legacy)
SELECT COUNT(*) INTO @defects_table
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'defects';

SELECT COUNT(*) INTO @coffee_id_col
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'defects' AND COLUMN_NAME = 'coffee_id';

SELECT COUNT(*) INTO @defects_fk
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db
  AND TABLE_NAME = 'defects'
  AND CONSTRAINT_NAME = 'FK_defects_coffee_id';

SET @q_defects = IF(
    @defects_table > 0 AND @coffee_id_col > 0 AND @defects_fk = 0,
    'ALTER TABLE defects ADD CONSTRAINT FK_defects_coffee_id FOREIGN KEY (coffee_id) REFERENCES coffees (id)',
    'SELECT 1'
);
PREPARE stmt_defects FROM @q_defects;
EXECUTE stmt_defects;
DEALLOCATE PREPARE stmt_defects;

-- profiles.user_id
SELECT COUNT(*) INTO @profiles_table
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'profiles';

SELECT COUNT(*) INTO @profiles_user_col
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'profiles' AND COLUMN_NAME = 'user_id';

SET @q_profiles_col = IF(
    @profiles_table > 0 AND @profiles_user_col = 0,
    'ALTER TABLE profiles ADD COLUMN user_id BIGINT',
    'SELECT 1'
);
PREPARE stmt_profiles_col FROM @q_profiles_col;
EXECUTE stmt_profiles_col;
DEALLOCATE PREPARE stmt_profiles_col;

SELECT COUNT(*) INTO @profiles_fk
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db
  AND TABLE_NAME = 'profiles'
  AND CONSTRAINT_NAME = 'FK_profiles_user_id';

SET @q_profiles_fk = IF(
    @profiles_table > 0 AND @profiles_fk = 0,
    'ALTER TABLE profiles ADD CONSTRAINT FK_profiles_user_id FOREIGN KEY (user_id) REFERENCES users (id)',
    'SELECT 1'
);
PREPARE stmt_profiles_fk FROM @q_profiles_fk;
EXECUTE stmt_profiles_fk;
DEALLOCATE PREPARE stmt_profiles_fk;

-- recipes.user_id → profiles
SELECT COUNT(*) INTO @recipes_table
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'recipes';

SELECT COUNT(*) INTO @recipes_fk
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db
  AND TABLE_NAME = 'recipes'
  AND CONSTRAINT_NAME = 'FK_recipe_user_id';

SET @q_recipes_fk = IF(
    @recipes_table > 0 AND @recipes_fk = 0,
    'ALTER TABLE recipes ADD CONSTRAINT FK_recipe_user_id FOREIGN KEY (user_id) REFERENCES profiles (id)',
    'SELECT 1'
);
PREPARE stmt_recipes_fk FROM @q_recipes_fk;
EXECUTE stmt_recipes_fk;
DEALLOCATE PREPARE stmt_recipes_fk;

-- ingredients.recipe_id → recipes
SELECT COUNT(*) INTO @ingredients_table
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'ingredients';

SELECT COUNT(*) INTO @ingredients_fk
FROM information_schema.TABLE_CONSTRAINTS
WHERE TABLE_SCHEMA = @db
  AND TABLE_NAME = 'ingredients'
  AND CONSTRAINT_NAME = 'FK_ingredients_recipe_id';

SET @q_ingredients_fk = IF(
    @ingredients_table > 0 AND @ingredients_fk = 0,
    'ALTER TABLE ingredients ADD CONSTRAINT FK_ingredients_recipe_id FOREIGN KEY (recipe_id) REFERENCES recipes (id)',
    'SELECT 1'
);
PREPARE stmt_ingredients_fk FROM @q_ingredients_fk;
EXECUTE stmt_ingredients_fk;
DEALLOCATE PREPARE stmt_ingredients_fk;
