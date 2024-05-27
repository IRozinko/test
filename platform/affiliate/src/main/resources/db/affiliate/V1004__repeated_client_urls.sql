SET search_path = affiliate, pg_catalog;

ALTER TABLE ONLY partner
  ADD COLUMN repeated_client_action_report_url text,
  ADD COLUMN repeated_client_lead_report_url text;

ALTER TABLE ONLY partner_audit
  ADD COLUMN repeated_client_action_report_url text,
  ADD COLUMN repeated_client_lead_report_url text;

UPDATE partner
SET repeated_client_action_report_url = action_report_url;

UPDATE partner
SET repeated_client_lead_report_url = lead_report_url;

ALTER TABLE ONLY lead
  ADD COLUMN repeated_client boolean not null default false;

ALTER TABLE ONLY lead_audit
  ADD COLUMN repeated_client boolean;
