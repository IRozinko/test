ALTER TABLE ONLY cms.item
  ADD COLUMN header_template text,
    ADD COLUMN footer_template text;

ALTER TABLE ONLY cms.item_audit
  ADD COLUMN header_template text,
    ADD COLUMN footer_template text;
