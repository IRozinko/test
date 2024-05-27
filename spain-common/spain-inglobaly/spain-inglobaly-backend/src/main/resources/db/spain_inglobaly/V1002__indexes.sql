-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_spain_inglobaly_response_created_at ON spain_inglobaly.response USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_response_application_id ON spain_inglobaly.response USING btree (application_id);
