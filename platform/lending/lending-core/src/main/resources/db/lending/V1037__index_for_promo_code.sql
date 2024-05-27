CREATE INDEX idx_loan_application_promo_code_id ON loan_application (promo_code_id);

create unique index idx_promo_code_unique_code on promo_code (code);
