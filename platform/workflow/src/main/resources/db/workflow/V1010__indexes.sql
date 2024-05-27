CREATE INDEX IF NOT EXISTS idx_activity_expires_at_id_active ON workflow.activity USING btree (expires_at, id)
  WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_trigger_next_attempt_at_id_waiting ON workflow.trigger USING btree (next_attempt_at, id)
  WHERE status = 'WAITING';

CREATE INDEX IF NOT EXISTS idx_trigger_activity_id ON workflow.trigger USING btree (activity_id);
