-- Migration script to create activity table
-- Date: 2025-01-XX

CREATE TABLE IF NOT EXISTS activity (
    id VARCHAR(255) PRIMARY KEY,
    profile_id VARCHAR(255) NOT NULL,
    activity_date DATE NOT NULL,
    count INTEGER NOT NULL DEFAULT 1,
    created_by VARCHAR(255),
    create_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    update_date TIMESTAMP,
    CONSTRAINT fk_activity_profile FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE,
    CONSTRAINT uk_activity_profile_date UNIQUE (profile_id, activity_date)
);

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_activity_profile_date ON activity(profile_id, activity_date);
CREATE INDEX IF NOT EXISTS idx_activity_date ON activity(activity_date);


