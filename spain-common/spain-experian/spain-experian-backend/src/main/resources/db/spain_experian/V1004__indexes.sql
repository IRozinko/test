-- OLD INDEXES
CREATE INDEX IF NOT EXISTS idx_spain_experian_operaciones_created_at ON spain_experian.cais_operaciones USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_spain_experian_resumen_created_at ON spain_experian.cais_resumen USING btree (created_at);
CREATE INDEX IF NOT EXISTS idx_cais_operaciones_application_id ON spain_experian.cais_operaciones USING btree (application_id);
CREATE INDEX IF NOT EXISTS idx_cais_resumen_application_id ON spain_experian.cais_resumen USING btree (application_id);
