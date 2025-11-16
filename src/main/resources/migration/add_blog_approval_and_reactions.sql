-- Migration script to add blog approval and reactions feature
-- Date: 2025-11-15

-- Add is_approved column to blogs table
ALTER TABLE blogs 
ADD COLUMN IF NOT EXISTS is_approved BOOLEAN NOT NULL DEFAULT false;

-- Create blog_reactions table
CREATE TABLE IF NOT EXISTS blog_reactions (
    id VARCHAR(255) PRIMARY KEY,
    blog_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    is_like BOOLEAN NOT NULL,
    create_date TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_date TIMESTAMP,
    update_by VARCHAR(255),
    CONSTRAINT fk_blog_reaction_blog FOREIGN KEY (blog_id) REFERENCES blogs(id) ON DELETE CASCADE,
    CONSTRAINT fk_blog_reaction_user FOREIGN KEY (user_id) REFERENCES profiles(id) ON DELETE CASCADE,
    CONSTRAINT uk_blog_reaction_user_blog UNIQUE (user_id, blog_id)
);

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_blog_reactions_blog_id ON blog_reactions(blog_id);
CREATE INDEX IF NOT EXISTS idx_blog_reactions_user_id ON blog_reactions(user_id);
CREATE INDEX IF NOT EXISTS idx_blog_reactions_is_like ON blog_reactions(is_like);

-- Update existing blogs to be approved (optional - uncomment if needed)
-- UPDATE blogs SET is_approved = true WHERE is_approved IS NULL OR is_approved = false;

