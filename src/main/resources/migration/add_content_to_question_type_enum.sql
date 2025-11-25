-- Migration: Add CONTENT to question_type enum constraint

-- Date: 2025-11-17

-- Description: 

--   - Drop existing check constraint on question.question_type

--   - Recreate constraint to include CONTENT as valid value



-- Drop the existing check constraint

ALTER TABLE question DROP CONSTRAINT IF EXISTS question_question_type_check;



-- Recreate the constraint with CONTENT included

ALTER TABLE question 

ADD CONSTRAINT question_question_type_check 

CHECK (question_type IN ('MULTIPLE_CHOICE', 'TRUE_FALSE', 'FILL_IN_BLANK', 'CONTENT'));



-- Verify the constraint

-- SELECT conname, pg_get_constraintdef(oid) 

-- FROM pg_constraint 

-- WHERE conrelid = 'question'::regclass AND conname = 'question_question_type_check';

