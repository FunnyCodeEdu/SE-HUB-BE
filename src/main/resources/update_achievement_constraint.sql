-- Script to update achievement_achievement_type_check constraint
-- Run this script on your PostgreSQL database to allow new achievement types

-- Drop the old constraint
ALTER TABLE achievement DROP CONSTRAINT IF EXISTS achievement_achievement_type_check;

-- Add new constraint with all current achievement types
ALTER TABLE achievement ADD CONSTRAINT achievement_achievement_type_check 
CHECK (achievement_type IN (
    -- Points
    'FRESH_DEV',
    'ACTIVE_LEARNER',
    'ADVANCED_CONTRIBUTOR',
    'ELITE_ENGINEER',
    
    -- Exams
    'FIRST_CHALLENGER',
    'HARDWORKING_CODER',
    'EXAM_GRINDER',
    'TEST_GRANDMASTER',
    
    -- Comments
    'DISCUSSION_STARTER',
    'HELPFUL_RESPONDER',
    'CORE_CONTRIBUTOR',
    'LEGENDARY_RESPONDER',
    
    -- Documents
    'RESOURCE_UPLOADER',
    'DOC_CONTRIBUTOR',
    'PRO_RESOURCE_CREATOR',
    'ARCHIVE_MASTER',
    
    -- Blogs Uploaded
    'FIRST_BLOGGER',
    'INSIGHT_WRITER',
    'TECH_BLOGGER',
    'MASTER_WRITER',
    
    -- Blogs Shared
    'SHARE_SUPPORTER',
    'COMMUNITY_PROMOTER',
    'COMMUNITY_AMBASSADOR',
    'COMMUNITY_EVANGELIST',
    
    -- Composite
    'WELL_ROUNDED_LEARNER',
    'COMMUNITY_HERO'
));

