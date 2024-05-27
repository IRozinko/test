CREATE TABLE lending.loan_contract (
  id                      BIGINT                      NOT NULL PRIMARY KEY,
  created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  created_by              TEXT,
  entity_version          BIGINT                      NOT NULL,
  updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  updated_by              TEXT,

  product_id              BIGINT                      NOT NULL,
  loan_id                 BIGINT                      NOT NULL,
  client_id               BIGINT                      NOT NULL,
  application_id          BIGINT                      NOT NULL,
  contract_date           DATE                        NOT NULL,
  effective_date          DATE                        NOT NULL,
  maturity_date           DATE                        NOT NULL,
  current                 BOOLEAN                     NOT NULL,
  period_count            INTEGER                     NOT NULL                             DEFAULT 0,
  period_unit             TEXT                        NOT NULL                             DEFAULT 'NA',
  number_of_installments  INTEGER                     NOT NULL                             DEFAULT 0,
  close_loan_on_paid      BOOLEAN                     NOT NULL                             DEFAULT TRUE,
  base_overdue_days       INTEGER                     NOT NULL                             DEFAULT 0,
  previous_contract_id    BIGINT,
  source_transaction_id   BIGINT,
  source_transaction_type TEXT
);


CREATE TABLE lending.loan_contract_audit (
  id                      BIGINT  NOT NULL,
  rev                     INTEGER NOT NULL,
  revtype                 SMALLINT,
  created_at              TIMESTAMP WITHOUT TIME ZONE,
  created_by              TEXT,
  updated_at              TIMESTAMP WITHOUT TIME ZONE,
  updated_by              TEXT,

  product_id              BIGINT  NOT NULL,
  loan_id                 BIGINT  NOT NULL,
  client_id               BIGINT  NOT NULL,
  application_id          BIGINT  NOT NULL,
  contract_date           DATE    NOT NULL,
  effective_date          DATE    NOT NULL,
  maturity_date           DATE    NOT NULL,
  current                 BOOLEAN NOT NULL,
  period_count            INTEGER NOT NULL,
  period_unit             TEXT    NOT NULL,
  number_of_installments  INTEGER NOT NULL,
  close_loan_on_paid      BOOLEAN NOT NULL,
  base_overdue_days       INTEGER NOT NULL,
  previous_contract_id    BIGINT,
  source_transaction_id   BIGINT,
  source_transaction_type TEXT
);

ALTER TABLE lending.installment
  ALTER schedule_id DROP NOT NULL,
  ALTER principal_scheduled DROP NOT NULL,
  ALTER interest_scheduled DROP NOT NULL,
  ALTER penalty_scheduled DROP NOT NULL,
  ALTER fee_scheduled DROP NOT NULL,
  ALTER total_scheduled DROP NOT NULL,
  ADD COLUMN total_invoiced NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN contract_id BIGINT,
  ADD COLUMN original_due_date DATE,
  ADD COLUMN dpd INTEGER NOT NULL DEFAULT 0;

ALTER TABLE lending.installment_audit
  ALTER schedule_id DROP NOT NULL,
  ALTER principal_scheduled DROP NOT NULL,
  ALTER interest_scheduled DROP NOT NULL,
  ALTER penalty_scheduled DROP NOT NULL,
  ALTER fee_scheduled DROP NOT NULL,
  ALTER total_scheduled DROP NOT NULL,
  ADD COLUMN total_invoiced NUMERIC(19, 4) NOT NULL DEFAULT 0,
  ADD COLUMN contract_id BIGINT,
  ADD COLUMN original_due_date DATE,
  ADD COLUMN dpd INTEGER NOT NULL DEFAULT 0;


ALTER TABLE ONLY lending.loan_contract
  ADD CONSTRAINT loan_contract_product_id FOREIGN KEY (product_id) REFERENCES lending.product(id);
ALTER TABLE ONLY lending.loan_contract
  ADD CONSTRAINT loan_contract_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan(id);
ALTER TABLE ONLY lending.loan_contract
  ADD CONSTRAINT loan_contract_application_id FOREIGN KEY (application_id) REFERENCES lending.loan_application(id);
ALTER TABLE ONLY lending.loan_contract
  ADD CONSTRAINT loan_contract_previous_contract_id FOREIGN KEY (previous_contract_id) REFERENCES lending.loan_contract(id);

ALTER TABLE ONLY lending.installment
  ADD CONSTRAINT installment_contract_id FOREIGN KEY (contract_id) REFERENCES lending.loan_contract(id);

CREATE INDEX idx_loan_contract_product_id ON lending.loan_contract USING btree (product_id);
CREATE INDEX idx_loan_contract_loan_id ON lending.loan_contract USING btree (loan_id);
CREATE INDEX idx_loan_contract_client_id ON lending.loan_contract USING btree (client_id);
CREATE INDEX idx_loan_contract_application_id ON lending.loan_contract USING btree (application_id);
CREATE INDEX idx_loan_contract_contract_id ON lending.installment USING btree (contract_id);
