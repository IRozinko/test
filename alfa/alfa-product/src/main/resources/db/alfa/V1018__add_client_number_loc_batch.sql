ALTER TABLE alfa.loc_batch
  ADD COLUMN client_number text NOT NULL default '',
  ADD COLUMN application_id bigint;

ALTER TABLE alfa.loc_batch_audit
  ADD COLUMN client_number text,
  ADD COLUMN application_id bigint;
