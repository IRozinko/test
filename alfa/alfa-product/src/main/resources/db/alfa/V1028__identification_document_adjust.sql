set search_path to alfa;

ALTER TABLE identification_document
  ALTER COLUMN back_file_id DROP NOT NULL,
  ALTER COLUMN back_file_name DROP NOT NULL,
  ALTER COLUMN expiration_date DROP NOT NULL,
  ALTER COLUMN surname_2 DROP NOT NULL,
  ALTER COLUMN surname_1 SET NOT NULL,
  DROP COLUMN resident_since,
  DROP COLUMN full_address,
  ADD COLUMN street TEXT,
  ADD COLUMN house TEXT,
  ADD COLUMN city TEXT,
  ADD COLUMN province TEXT;

ALTER TABLE identification_document_audit
  DROP COLUMN resident_since,
  DROP COLUMN full_address,
  ADD COLUMN street TEXT,
  ADD COLUMN house TEXT,
  ADD COLUMN city TEXT,
  ADD COLUMN province TEXT;
