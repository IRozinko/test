ALTER TABLE dc.debt
  ADD COLUMN batch_assignment_required boolean not null default false;
