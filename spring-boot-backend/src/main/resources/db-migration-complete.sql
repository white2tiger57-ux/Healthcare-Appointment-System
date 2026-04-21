-- =============================================
-- CLEAN MIGRATION SCRIPT
-- Run on a fresh database or after DROP DATABASE
-- =============================================

-- Temporarily disable foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- Drop all tables in dependency order
-- =============================================
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_preferences;
DROP TABLE IF EXISTS doctor_schedule;
DROP TABLE IF EXISTS doctor_department;
DROP TABLE IF EXISTS health_metrics;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS conversations;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS medical_record;
DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS department;

-- =============================================
-- Create tables (all with BIGINT IDs)
-- =============================================

-- 1. patients
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) NOT NULL,
    Mobile_number VARCHAR(15) NOT NULL,
    Age INT NOT NULL,
    Gender VARCHAR(15) NOT NULL,
    Blood_group VARCHAR(3) NOT NULL,
    Weight INT NOT NULL,
    Height DECIMAL(5,2),
    Medical_history TEXT,
    Address TEXT NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255),
    Photo VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. doctors
CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(50) NOT NULL,
    Contact VARCHAR(15) NOT NULL,
    Qualification TEXT NOT NULL,
    Specialization TEXT NOT NULL,
    Availability VARCHAR(50) NOT NULL DEFAULT 'Full-time',
    Location TEXT NOT NULL,
    Email VARCHAR(255) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Experience INT DEFAULT 0,
    Photo VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. department
CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. doctor_department (join table)
CREATE TABLE doctor_department (
    doctor_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    PRIMARY KEY (doctor_id, department_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. appointment
CREATE TABLE appointment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(50) DEFAULT 'Scheduled',
    service_type VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. medical_record
CREATE TABLE medical_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    diagnosis TEXT,
    prescription TEXT,
    file_path VARCHAR(255),
    record_type VARCHAR(100),
    description TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. users (Spring Security)
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('patient', 'doctor', 'admin') NOT NULL,
    related_id BIGINT NOT NULL,
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. roles
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. user_roles
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. doctor_schedule
CREATE TABLE doctor_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    day_of_week INT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. health_metrics
CREATE TABLE health_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    heart_rate INT,
    systolic INT,
    diastolic INT,
    temperature DOUBLE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. notifications
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    category VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    is_urgent BOOLEAN DEFAULT FALSE,
    related_id BIGINT,
    action_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. messages
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 14. conversations
CREATE TABLE conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    participant1_id BIGINT NOT NULL,
    participant2_id BIGINT NOT NULL,
    last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 15. feedback
CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT,
    rating INT NOT NULL,
    comment TEXT,
    feedback_type VARCHAR(50),
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 16. user_preferences
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    app_notifications BOOLEAN DEFAULT TRUE,
    reminder_time VARCHAR(20) DEFAULT '24h'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- SEED DATA
-- =============================================

-- Roles
INSERT INTO roles (name) VALUES ('ROLE_PATIENT'), ('ROLE_DOCTOR'), ('ROLE_ADMIN');

-- Departments
INSERT INTO department (name) VALUES
('Cardiology'), ('Neurology'), ('Orthopedics'), ('Pediatrics'),
('Dermatology'), ('Ophthalmology'), ('ENT'), ('General Medicine'),
('Gynecology'), ('Oncology');

-- Sample Doctor (id will be 1)
INSERT INTO doctors (Name, Contact, Qualification, Specialization, Availability, Location, Email, Password, Experience)
VALUES ('Dr. Smith', '9876543210', 'MBBS, MD', 'Cardiology', 'Full-time', 'Room 101, Building A', 'doctor@healthcare.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 10);

-- Link doctor to Cardiology department (id=1)
INSERT INTO doctor_department (doctor_id, department_id) VALUES (1, 1);

-- Doctor schedule (Mon-Fri, 9am-5pm)
INSERT INTO doctor_schedule (doctor_id, day_of_week, start_time, end_time) VALUES
(1, 1, '09:00:00', '17:00:00'),
(1, 2, '09:00:00', '17:00:00'),
(1, 3, '09:00:00', '17:00:00'),
(1, 4, '09:00:00', '17:00:00'),
(1, 5, '09:00:00', '17:00:00');

-- Sample Patient (id will be 1)
INSERT INTO patients (Name, Mobile_number, Age, Gender, Blood_group, Weight, Address, Email, Password)
VALUES ('John Doe', '9123456789', 30, 'Male', 'O+', 70, '123 Main Street', 'patient@healthcare.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Users table (password = "password123")
INSERT INTO users (email, password, user_type, related_id, full_name) VALUES
('admin@healthcare.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin', 0, 'System Admin'),
('doctor@healthcare.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'doctor', 1, 'Dr. Smith'),
('patient@healthcare.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'patient', 1, 'John Doe');

-- Assign roles (role ids: 1=ROLE_PATIENT, 2=ROLE_DOCTOR, 3=ROLE_ADMIN)
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 3),  -- admin
(2, 2),  -- doctor
(3, 1);  -- patient

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Done
SELECT 'Migration completed successfully!' AS status;