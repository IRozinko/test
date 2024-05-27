ALTER TABLE nordigen.log ADD COLUMN requested_at timestamp with time zone not null default now();

update nordigen.log set requested_at = created_at;
