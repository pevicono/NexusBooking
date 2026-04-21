-- =============================================================
-- NexusBooking – Flyway Migration V4
-- Creates groups and group_members tables.
-- =============================================================

CREATE TABLE IF NOT EXISTS groups (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    description TEXT,
    owner_id    BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS group_members (
    id          BIGSERIAL       PRIMARY KEY,
    group_id    BIGINT          NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role        VARCHAR(20)     NOT NULL DEFAULT 'MEMBER'
                                CHECK (role IN ('OWNER', 'MEMBER')),
    joined_at   TIMESTAMP       NOT NULL DEFAULT NOW(),
    UNIQUE(group_id, user_id)
);

DROP TRIGGER IF EXISTS trg_groups_updated_at ON groups;
CREATE TRIGGER trg_groups_updated_at
    BEFORE UPDATE ON groups
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
