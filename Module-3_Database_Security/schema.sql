-- ====================================================================
-- CampusVoice Database Schema
-- Anonymous Student Issue Reporting & Class-Level Verification System
-- ====================================================================

CREATE DATABASE IF NOT EXISTS campusvoice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campusvoice;

-- Drop tables in reverse order of foreign keys
DROP TABLE IF EXISTS reverification_votes;
DROP TABLE IF EXISTS evidence;
DROP TABLE IF EXISTS issue_status;
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS classes;

-- --------------------------------------------------------------------
-- 1. Classes Table
-- Stores academic class information and total student strength
-- --------------------------------------------------------------------
CREATE TABLE classes (
    class_id INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    semester INT NOT NULL,
    section VARCHAR(10) NOT NULL,
    total_strength INT NOT NULL DEFAULT 60,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------------------
-- 2. Users Table
-- Institutional credentials for student eligibility and admin access
-- --------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(50) UNIQUE NOT NULL,      -- Institutional Roll No / Username
    password_hash VARCHAR(255) NOT NULL,         -- SHA-256 hashed password
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    class_id INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE SET NULL
);

-- --------------------------------------------------------------------
-- 3. Issues Table
-- STRICT IDENTITY DECOUPLING: No student_id / user_id foreign key!
-- Only class_id is stored to enable peer verification.
-- --------------------------------------------------------------------
CREATE TABLE issues (
    issue_id VARCHAR(20) PRIMARY KEY,            -- e.g. 'CV-1847'
    class_id INT NOT NULL,                       -- Target class for peer verification
    category VARCHAR(50) NOT NULL,               -- e.g. Academics, IT Lab, Maintenance, Hostel, Canteen
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') NOT NULL DEFAULT 'MEDIUM',
    status ENUM('REPORTED', 'UNDER_VERIFICATION', 'CLASS_VERIFIED', 'UNDER_INVESTIGATION', 'RESOLVED', 'REOPENED') NOT NULL DEFAULT 'UNDER_VERIFICATION',
    department_assigned VARCHAR(100) DEFAULT NULL, -- e.g. IT Lab, Maintenance, Academics
    resolution_remarks TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(class_id) ON DELETE RESTRICT
);

-- --------------------------------------------------------------------
-- 4. Votes Table
-- Anonymous peer voting with one-way hashed token to prevent duplicates
-- --------------------------------------------------------------------
CREATE TABLE votes (
    vote_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id VARCHAR(20) NOT NULL,
    response ENUM('FREQUENTLY', 'SOMETIMES', 'NEVER') NOT NULL,
    voter_token_hash VARCHAR(64) NOT NULL,        -- SHA-256(student_id + issue_id + salt)
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE,
    UNIQUE KEY unique_issue_voter (issue_id, voter_token_hash)
);

-- --------------------------------------------------------------------
-- 5. Issue Status History Table (Audit Trail)
-- --------------------------------------------------------------------
CREATE TABLE issue_status (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id VARCHAR(20) NOT NULL,
    old_status VARCHAR(50) DEFAULT NULL,
    new_status VARCHAR(50) NOT NULL,
    remarks TEXT DEFAULT NULL,
    updated_by_role VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE
);

-- --------------------------------------------------------------------
-- 6. Evidence Table
-- Supporting attachments for issues
-- --------------------------------------------------------------------
CREATE TABLE evidence (
    evidence_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id VARCHAR(20) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE
);

-- --------------------------------------------------------------------
-- 7. Reverification Votes Table
-- Post-resolution student feedback
-- --------------------------------------------------------------------
CREATE TABLE reverification_votes (
    reverify_id INT AUTO_INCREMENT PRIMARY KEY,
    issue_id VARCHAR(20) NOT NULL,
    response ENUM('RESOLVED', 'PARTIALLY_RESOLVED', 'STILL_FACING') NOT NULL,
    voter_token_hash VARCHAR(64) NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues(issue_id) ON DELETE CASCADE,
    UNIQUE KEY unique_issue_reverify (issue_id, voter_token_hash)
);

-- Indexes for performance
CREATE INDEX idx_issues_class ON issues(class_id);
CREATE INDEX idx_issues_status ON issues(status);
CREATE INDEX idx_votes_issue ON votes(issue_id);
CREATE INDEX idx_status_issue ON issue_status(issue_id);

-- ====================================================================
-- Sample Seed Data
-- ====================================================================

-- Classes
INSERT INTO classes (class_id, class_name, department, semester, section, total_strength) VALUES
(1, 'B.Tech AI & DS - Sec C', 'AI & DS', 5, 'C', 60),
(2, 'B.Tech CSE - Sec A', 'CSE', 5, 'A', 65),
(3, 'B.Tech IT - Sec B', 'IT', 3, 'B', 55);

-- Users (Passwords are SHA-256 for 'password123': ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f)
INSERT INTO users (user_id, student_id, password_hash, full_name, email, role, class_id) VALUES
(1, '24EARAD163', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Tavish Bhanawat', 'tavish@campus.edu', 'STUDENT', 1),
(2, '24EARAD164', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Yash Raj', 'yash@campus.edu', 'STUDENT', 1),
(3, '24EARAD165', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Aman Sharma', 'aman@campus.edu', 'STUDENT', 1),
(4, 'ADMIN001',   'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'College Administrator', 'admin@campus.edu', 'ADMIN', NULL);

-- Initial sample issue
INSERT INTO issues (issue_id, class_id, category, title, description, priority, status, department_assigned) VALUES
('CV-1001', 1, 'IT Lab', 'Projector and AC not functioning in Lab 304', 'During practical sessions the projector frequently turns off and air conditioning is broken.', 'HIGH', 'UNDER_VERIFICATION', 'IT Lab');

INSERT INTO issue_status (issue_id, old_status, new_status, remarks, updated_by_role) VALUES
('CV-1001', NULL, 'UNDER_VERIFICATION', 'Issue reported anonymously and opened for peer voting', 'SYSTEM');
