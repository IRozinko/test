CREATE TABLE alfa.popup_attribute
(
  popup_id bigint NOT NULL,
  key      text   NOT NULL,
  value    text
);

CREATE TABLE alfa.popup_attribute_audit
(
  rev      integer NOT NULL,
  revtype  smallint,
  popup_id bigint  NOT NULL,
  value    text    NOT NULL,
  key      text    NOT NULL
);

ALTER TABLE ONLY alfa.popup_attribute
  ADD CONSTRAINT popup_attribute_pkey PRIMARY KEY (popup_id, key),
  ADD CONSTRAINT fk_popup_attribute_client_id FOREIGN KEY (popup_id) REFERENCES alfa.popup (id) ON DELETE CASCADE;

ALTER TABLE ONLY alfa.popup_attribute_audit
  ADD CONSTRAINT fk_popup_attribute_audit_rev_id FOREIGN KEY (rev) REFERENCES common.revision (id);

CREATE INDEX IF NOT EXISTS idx_popup_attribute_client_id ON alfa.popup_attribute USING btree (popup_id);
