ALTER TABLE crm.country
  RENAME COLUMN country TO name;

ALTER TABLE crm.country
  RENAME COLUMN country_display_name TO display_name;

ALTER TABLE crm.country
  ADD COLUMN code CHAR(2) NOT NULL DEFAULT 'XX',
  ADD COLUMN home_country BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE crm.identity_document
  ADD COLUMN nationality_id bigint,
  ADD CONSTRAINT fk_identity_document_nationality FOREIGN KEY (nationality_id) REFERENCES crm.country (id);

ALTER TABLE crm.identity_document_audit
  ADD COLUMN nationality_id bigint;

CREATE UNIQUE INDEX IF NOT EXISTS idx_country_code ON crm.country USING btree (code);
