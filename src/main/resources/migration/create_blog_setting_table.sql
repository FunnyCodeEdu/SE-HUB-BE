-- Migration script to create blog_setting table
-- Date: 2025-01-XX

CREATE TABLE IF NOT EXISTS blog_setting (
    id VARCHAR(255) PRIMARY KEY,
    require_approval BOOLEAN NOT NULL DEFAULT true,
    created_by VARCHAR(255),
    create_date TIMESTAMP NOT NULL,
    updated_by VARCHAR(255),
    update_date TIMESTAMP
);

-- Insert default setting (singleton record)
INSERT INTO blog_setting (id, require_approval, created_by, create_date, updated_by, update_date)
VALUES ('BLOG_SETTING_SINGLETON', true, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


