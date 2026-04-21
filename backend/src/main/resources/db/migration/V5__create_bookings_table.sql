-- =============================================================
-- NexusBooking – Flyway Migration V5
-- Creates bookings table.
-- =============================================================

CREATE TABLE IF NOT EXISTS bookings (
    id           BIGSERIAL   PRIMARY KEY,
    user_id      BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    facility_id  BIGINT      NOT NULL REFERENCES facilities(id) ON DELETE CASCADE,
    group_id     BIGINT      REFERENCES groups(id) ON DELETE SET NULL,
    start_time   TIMESTAMP   NOT NULL,
    end_time     TIMESTAMP   NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED'
                             CHECK (status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED')),
    notes        TEXT,
    created_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_booking_times CHECK (end_time > start_time)
);

DROP TRIGGER IF EXISTS trg_bookings_updated_at ON bookings;
CREATE TRIGGER trg_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
