-- =============================================================
-- NexusBooking - Flyway Migration V7
-- Adds short join code for groups so users can join with a non-sequential code.
-- =============================================================

ALTER TABLE groups
    ADD COLUMN IF NOT EXISTS join_code VARCHAR(16);

UPDATE groups
SET join_code = UPPER(SUBSTRING(MD5((id::text || NOW()::text)) FROM 1 FOR 8))
WHERE join_code IS NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_groups_join_code'
    ) THEN
        ALTER TABLE groups
            ADD CONSTRAINT uq_groups_join_code UNIQUE (join_code);
    END IF;
END $$;
