ALTER TABLE lending.invoice_item_audit
  DROP COLUMN fee_id;
ALTER TABLE lending.invoice_item
  DROP COLUMN fee_id;

DROP TABLE lending.fee_audit;
DROP TABLE lending.fee;

CREATE TABLE lending.installment (
  id                        INT8                     NOT NULL,
  created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by                TEXT,
  entity_version            INT8                     NOT NULL,
  updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by                TEXT,
  apply_penalty             BOOLEAN                  NOT NULL,
  cash_in                   NUMERIC(19, 4)           NOT NULL,
  client_id                 INT8                     NOT NULL,
  close_date                DATE,
  close_reason              TEXT,
  due_date                  DATE                     NOT NULL,
  fee_applied               NUMERIC(19, 4)           NOT NULL,
  fee_invoiced              NUMERIC(19, 4)           NOT NULL,
  fee_paid                  NUMERIC(19, 4)           NOT NULL,
  fee_scheduled             NUMERIC(19, 4)           NOT NULL,
  fee_written_off           NUMERIC(19, 4)           NOT NULL,
  generate_invoice_on_date  DATE,
  grace_period_in_days      INT8                     NOT NULL,
  installment_number        TEXT                     NOT NULL,
  installment_sequence      INT8                     NOT NULL,
  interest_applied          NUMERIC(19, 4)           NOT NULL,
  interest_invoiced         NUMERIC(19, 4)           NOT NULL,
  interest_paid             NUMERIC(19, 4)           NOT NULL,
  interest_scheduled        NUMERIC(19, 4)           NOT NULL,
  interest_written_off      NUMERIC(19, 4)           NOT NULL,
  invoice_file_generated_at TIMESTAMP WITH TIME ZONE,
  invoice_file_id           INT8,
  invoice_file_name         TEXT,
  invoice_file_sent_at      TIMESTAMP WITH TIME ZONE,
  overpayment_used          NUMERIC(19, 4)           NOT NULL,
  penalty_applied           NUMERIC(19, 4)           NOT NULL,
  penalty_invoiced          NUMERIC(19, 4)           NOT NULL,
  penalty_paid              NUMERIC(19, 4)           NOT NULL,
  penalty_scheduled         NUMERIC(19, 4)           NOT NULL,
  penalty_written_off       NUMERIC(19, 4)           NOT NULL,
  period_from               DATE                     NOT NULL,
  period_to                 DATE                     NOT NULL,
  principal_invoiced        NUMERIC(19, 4)           NOT NULL,
  principal_paid            NUMERIC(19, 4)           NOT NULL,
  principal_scheduled       NUMERIC(19, 4)           NOT NULL,
  principal_written_off     NUMERIC(19, 4)           NOT NULL,
  status                    TEXT                     NOT NULL,
  status_detail             TEXT                     NOT NULL,
  total_due                 NUMERIC(19, 4)           NOT NULL,
  total_paid                NUMERIC(19, 4)           NOT NULL,
  total_scheduled           NUMERIC(19, 4)           NOT NULL,
  value_date                DATE                     NOT NULL,
  loan_id                   INT8                     NOT NULL,
  schedule_id               INT8                     NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE lending.installment_audit (
  id                        INT8 NOT NULL,
  rev                       INT4 NOT NULL,
  revtype                   INT2,
  created_at                TIMESTAMP WITH TIME ZONE,
  created_by                TEXT,
  updated_at                TIMESTAMP WITH TIME ZONE,
  updated_by                TEXT,
  apply_penalty             BOOLEAN,
  cash_in                   NUMERIC(19, 4),
  client_id                 INT8,
  close_date                DATE,
  close_reason              TEXT,
  due_date                  DATE,
  fee_applied               NUMERIC(19, 4),
  fee_invoiced              NUMERIC(19, 4),
  fee_paid                  NUMERIC(19, 4),
  fee_scheduled             NUMERIC(19, 4),
  fee_written_off           NUMERIC(19, 4),
  generate_invoice_on_date  DATE,
  grace_period_in_days      INT8,
  installment_number        TEXT,
  installment_sequence      INT8,
  interest_applied          NUMERIC(19, 4),
  interest_invoiced         NUMERIC(19, 4),
  interest_paid             NUMERIC(19, 4),
  interest_scheduled        NUMERIC(19, 4),
  interest_written_off      NUMERIC(19, 4),
  invoice_file_generated_at TIMESTAMP WITH TIME ZONE,
  invoice_file_id           INT8,
  invoice_file_name         TEXT,
  invoice_file_sent_at      TIMESTAMP WITH TIME ZONE,
  overpayment_used          NUMERIC(19, 4),
  penalty_applied           NUMERIC(19, 4),
  penalty_invoiced          NUMERIC(19, 4),
  penalty_paid              NUMERIC(19, 4),
  penalty_scheduled         NUMERIC(19, 4),
  penalty_written_off       NUMERIC(19, 4),
  period_from               DATE,
  period_to                 DATE,
  principal_invoiced        NUMERIC(19, 4),
  principal_paid            NUMERIC(19, 4),
  principal_scheduled       NUMERIC(19, 4),
  principal_written_off     NUMERIC(19, 4),
  status                    TEXT,
  status_detail             TEXT,
  total_due                 NUMERIC(19, 4),
  total_paid                NUMERIC(19, 4),
  total_scheduled           NUMERIC(19, 4),
  value_date                DATE,
  loan_id                   INT8,
  schedule_id               INT8,
  PRIMARY KEY (id, rev)
);


CREATE INDEX idx_installment_client_id
  ON lending.installment (client_id);
CREATE INDEX idx_installment_loan_id
  ON lending.installment (loan_id);
CREATE INDEX idx_installment_schedule_id
  ON lending.installment (schedule_id);

ALTER TABLE lending.installment
  ADD CONSTRAINT FKddvr1rongdlfl3pmj87eg48cy
FOREIGN KEY (loan_id)
REFERENCES lending.loan;

ALTER TABLE lending.installment
  ADD CONSTRAINT FK2spn7u0qluyctvvpggubboitn
FOREIGN KEY (schedule_id)
REFERENCES lending.schedule;

ALTER TABLE lending.installment_audit
  ADD CONSTRAINT FK9kjwdyikeng0b0oa6wy7xtxj1
FOREIGN KEY (rev)
REFERENCES common.revision;

alter table lending.installment
  add constraint uq_installment_number unique (installment_number);
