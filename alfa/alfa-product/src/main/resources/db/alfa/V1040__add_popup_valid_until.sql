ALTER TABLE alfa.popup
  ADD COLUMN valid_until TIMESTAMP WITH TIME ZONE;

ALTER TABLE alfa.popup_audit
  ADD COLUMN valid_until TIMESTAMP WITH TIME ZONE;
