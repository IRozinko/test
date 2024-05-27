ALTER TABLE alfa.popup
  ADD COLUMN resolved_at TIMESTAMP;

ALTER TABLE alfa.popup_audit
  ADD COLUMN resolved_at TIMESTAMP;

UPDATE alfa.popup
SET resolved_at = updated_at
WHERE resolution != 'NONE';
