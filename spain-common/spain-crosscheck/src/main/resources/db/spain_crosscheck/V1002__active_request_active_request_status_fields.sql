ALTER TABLE spain_crosscheck.log ADD COLUMN active_request BOOL NOT NULL DEFAULT FALSE;
ALTER TABLE spain_crosscheck.log ADD COLUMN active_request_status TEXT;
