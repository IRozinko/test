SET search_path = alfa;

ALTER TABLE alfa.loc_batch
  ADD COLUMN status_detail text;

DROP INDEX idx_loc_batch_client_id;

ALTER TABLE alfa.loc_batch_audit
  ADD COLUMN status_detail text;
