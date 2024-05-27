-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_spain_equifax_created_at ON spain_equifax.equifax USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_equifax_application_id ON spain_equifax.equifax USING btree (application_id);
