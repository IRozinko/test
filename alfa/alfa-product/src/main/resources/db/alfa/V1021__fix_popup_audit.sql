ALTER TABLE ONLY alfa.popup_audit
  ADD COLUMN rev integer NOT NULL,
  ADD COLUMN revtype smallint,
  DROP COLUMN entity_version,
  DROP CONSTRAINT popup_audit_pkey,
  ADD CONSTRAINT fk_popup_audit_rev_id FOREIGN KEY (rev) REFERENCES common.revision (id);
