-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_activity_client_id ON activity.activity USING btree (client_id);
