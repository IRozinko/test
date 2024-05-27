ALTER TABLE web.special_link
  ADD COLUMN expires_at TIMESTAMP NOT NULL DEFAULT now() + interval '2' hour;

ALTER TABLE web.special_link_audit
  ADD COLUMN rev integer NOT NULL DEFAULT 0,
  ADD COLUMN type text NOT NULL DEFAULT 'TEXT',
  ADD COLUMN revtype smallint,
  ADD COLUMN expires_at TIMESTAMP,
  DROP COLUMN entity_version,
  DROP CONSTRAINT special_link_audit_pkey,
  ADD CONSTRAINT special_link_audit_pkey PRIMARY KEY (id, rev),
  ADD CONSTRAINT fk_special_link_audit_rev FOREIGN KEY (rev) REFERENCES common.revision (id);

UPDATE web.special_link
SET special_link_type = 'ADD_PAYMENT'
WHERE special_link_type = 'SPECIAL_LINK_TYPE_DEBT';

UPDATE web.special_link
SET special_link_type = 'LOC_SPECIAL_OFFER'
WHERE special_link_type = 'LocSpecialOfferLink';
