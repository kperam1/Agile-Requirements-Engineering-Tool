-- UC-07: Story Estimates table for MySQL
-- Assumes existing table `user_stories (id bigint primary key)`

CREATE TABLE IF NOT EXISTS story_estimates (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_story_id BIGINT NOT NULL,
  points INT NOT NULL,
  estimated_by VARCHAR(255),
  estimated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_story_estimates_story FOREIGN KEY (user_story_id)
    REFERENCES user_stories(id) ON DELETE CASCADE
);

CREATE INDEX idx_story_estimates_story ON story_estimates(user_story_id);
CREATE INDEX idx_story_estimates_by ON story_estimates(estimated_by);
