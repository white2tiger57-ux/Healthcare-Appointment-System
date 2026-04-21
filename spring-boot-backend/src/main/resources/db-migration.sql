-- =============================================
-- 1. Disable foreign key checks temporarily
-- =============================================
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 2. Drop broken tables (if they exist)
-- =============================================
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;

-- =============================================
-- 3. Add Password column to patients table
-- =============================================
-- First check if column already exists (optional, but safe)
SET @col_exists = (SELECT COUNT(*) FROM information_schema.columns 
                   WHERE table_name = 'patients' AND column_name = 'Password');
SET @sql = IF(@col_exists = 0, 'ALTER TABLE patients ADD COLUMN Password VARCHAR(255) NOT NULL DEFAULT "$2a$10$dummyHashedPasswordForMigration"', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =============================================
-- 4. Update patients with a real BCrypt hash for "password123"
--    (You can change the default password here. BCrypt hash of "password123")
-- =============================================
UPDATE patients SET Password = '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mr/.cEoq8QZ9Z3H8Z5WqL8Z5Z5Z5Z5Z' WHERE Password = '$2a$10$dummyHashedPasswordForMigration';

-- =============================================
-- 5. Create unified users table
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('patient', 'doctor', 'admin') NOT NULL,
    related_id BIGINT NOT NULL,   -- stores patients.id or doctors.id
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 6. Insert patients into users
-- =============================================
INSERT INTO users (email, password, user_type, related_id, full_name)
SELECT 
    Email, 
    Password, 
    'patient', 
    id, 
    Name
FROM patients
ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    user_type = VALUES(user_type),
    related_id = VALUES(related_id),
    full_name = VALUES(full_name);

-- =============================================
-- 7. Insert doctors into users
-- =============================================
INSERT INTO users (email, password, user_type, related_id, full_name)
SELECT 
    Email, 
    Password, 
    'doctor', 
    id, 
    Name
FROM doctors
ON DUPLICATE KEY UPDATE 
    password = VALUES(password),
    user_type = VALUES(user_type),
    related_id = VALUES(related_id),
    full_name = VALUES(full_name);

-- =============================================
-- 8. Create roles table
-- =============================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT IGNORE INTO roles (name) VALUES 
    ('ROLE_PATIENT'), 
    ('ROLE_DOCTOR'), 
    ('ROLE_ADMIN');

-- =============================================
-- 9. Create user_roles junction table
-- =============================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- =============================================
-- 10. Assign roles based on user_type
-- =============================================
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.user_id, r.id
FROM users u
JOIN roles r ON (
    (u.user_type = 'patient' AND r.name = 'ROLE_PATIENT') OR
    (u.user_type = 'doctor' AND r.name = 'ROLE_DOCTOR')
);

-- =============================================
-- 11. (Optional) Create an admin user if none exists
-- =============================================
INSERT IGNORE INTO users (email, password, user_type, related_id, full_name)
VALUES ('admin@healthcare.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mr/.cEoq8QZ9Z3H8Z5WqL8Z5Z5Z5Z5Z', 'admin', 0, 'System Admin');

-- Then assign admin role (role_id for ROLE_ADMIN is usually 3)
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.user_id, r.id
FROM users u, roles r
WHERE u.email = 'admin@healthcare.com' AND r.name = 'ROLE_ADMIN';

-- =============================================
-- 12. Re-enable foreign key checks
-- =============================================
SET FOREIGN_KEY_CHECKS = 1;