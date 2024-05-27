set search_path to alfa;

ALTER TABLE identification_document
  ADD COLUMN is_valid BOOLEAN NOT NULL DEFAULT FALSE,
  ADD COLUMN validated_at TIMESTAMP WITH TIME ZONE NULL;

ALTER TABLE identification_document_audit
  ADD COLUMN is_valid BOOLEAN,
  ADD COLUMN validated_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE alfa.identification_document ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'UTC';
ALTER TABLE alfa.identification_document ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE USING updated_at AT TIME ZONE 'UTC';
