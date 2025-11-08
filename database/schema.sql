-- ============================================
-- IdeaBoard Database Schema
-- MySQL Database Setup
-- ============================================

-- Create database (if not exists)
CREATE DATABASE IF NOT EXISTS ideaboard_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE ideaboard_db;

-- Drop existing table if needed (for fresh start)
-- DROP TABLE IF EXISTS ideas;

-- Create ideas table
CREATE TABLE IF NOT EXISTS ideas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    owner_name VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_owner (owner_name),
    INDEX idx_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
INSERT INTO ideas (title, category, description, status, owner_name) VALUES
('Implement Dark Mode', 'Product Enhancement', 'Add dark mode support across the entire application for better user experience in low-light environments.', 'New', 'John Doe'),
('Mobile App Development', 'New Feature', 'Develop a mobile application for iOS and Android platforms to increase accessibility.', 'Under Review', 'Jane Smith'),
('Performance Optimization', 'Process Improvement', 'Optimize database queries and implement caching to improve application response time.', 'In Progress', 'Mike Johnson'),
('User Analytics Dashboard', 'New Feature', 'Create a comprehensive analytics dashboard for tracking user behavior and engagement metrics.', 'Approved', 'Sarah Williams'),
('API Rate Limiting', 'Product Enhancement', 'Implement rate limiting on public APIs to prevent abuse and ensure fair usage.', 'New', 'David Brown');

-- Verify data
SELECT * FROM ideas ORDER BY created_at DESC;
