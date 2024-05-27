ALTER TABLE instantor.response
  ADD COLUMN processing_status TEXT NOT NULL DEFAULT 'PENDING';

UPDATE instantor.response
  SET processing_status = 'PROCESSED';
