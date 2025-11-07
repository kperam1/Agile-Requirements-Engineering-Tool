-- Stories table to store Acceptance Criteria and related fields
CREATE TABLE IF NOT EXISTS stories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  acceptance_criteria TEXT,
  assignee VARCHAR(100),
  story_points INT DEFAULT 1,
  priority VARCHAR(20) DEFAULT 'Medium',   -- High | Medium | Low
  status VARCHAR(20) DEFAULT 'To Do',      -- To Do | In Progress | New | Closed
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
