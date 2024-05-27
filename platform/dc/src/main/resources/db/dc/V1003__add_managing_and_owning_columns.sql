ALTER TABLE dc.debt
  ADD COLUMN managing_company TEXT;

ALTER TABLE dc.debt
  ADD COLUMN owning_company TEXT;

ALTER TABLE dc.action
  ADD COLUMN managing_company_before TEXT;

ALTER TABLE dc.action
  ADD COLUMN managing_company_after TEXT;

ALTER TABLE dc.action
  ADD COLUMN owning_company_before TEXT;

ALTER TABLE dc.action
  ADD COLUMN owning_company_after TEXT;
