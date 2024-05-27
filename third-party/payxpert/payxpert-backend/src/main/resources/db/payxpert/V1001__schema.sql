CREATE TABLE payxpert.credit_card (
  id                         INT8                     NOT NULL,
  created_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by                 TEXT,
  entity_version             INT8                     NOT NULL,
  updated_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by                 TEXT,
  active                     BOOLEAN                  NOT NULL,
  callback_transaction_id    TEXT                     NOT NULL,
  card_brand                 TEXT,
  card_expire_month          INT8,
  card_expire_year           INT8,
  card_holder_name           TEXT,
  card_is3dsecure            BOOLEAN,
  card_number                TEXT,
  client_id                  INT8                     NOT NULL,
  recurring_payments_enabled BOOLEAN                  NOT NULL,
  payment_request_id         INT8                     NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE payxpert.credit_card_audit (
  id                         INT8 NOT NULL,
  rev                        INT4 NOT NULL,
  revtype                    INT2,
  created_at                 TIMESTAMP WITH TIME ZONE,
  created_by                 TEXT,
  updated_at                 TIMESTAMP WITH TIME ZONE,
  updated_by                 TEXT,
  active                     BOOLEAN,
  callback_transaction_id    TEXT,
  card_brand                 TEXT,
  card_expire_month          INT8,
  card_expire_year           INT8,
  card_holder_name           TEXT,
  card_is3dsecure            BOOLEAN,
  card_number                TEXT,
  client_id                  INT8,
  recurring_payments_enabled BOOLEAN,
  payment_request_id         INT8,
  PRIMARY KEY (id, rev)
);

CREATE TABLE payxpert.payment_request (
  id                        INT8                     NOT NULL,
  created_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by                TEXT,
  entity_version            INT8                     NOT NULL,
  updated_at                TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by                TEXT,
  amount                    NUMERIC(19, 2)           NOT NULL,
  callback_received_at      TIMESTAMP WITH TIME ZONE,
  callback_transaction_id   TEXT,
  card_brand                TEXT,
  card_expire_month         INT8,
  card_expire_year          INT8,
  card_holder_name          TEXT,
  card_is3dsecure           BOOLEAN,
  card_number               TEXT,
  client_id                 INT8                     NOT NULL,
  ctrl_callback_url         TEXT                     NOT NULL,
  ctrl_redirect_url         TEXT,
  currency                  TEXT                     NOT NULL,
  customer_redirect_url     TEXT                     NOT NULL,
  customer_token            TEXT,
  enable_recurring_payments BOOLEAN                  NOT NULL,
  error_code                TEXT,
  error_message             TEXT,
  invoice_id                INT8,
  last_status_check_at      TIMESTAMP WITH TIME ZONE,
  loan_id                   INT8,
  merchant_token            TEXT,
  operation                 TEXT                     NOT NULL,
  order_id                  TEXT                     NOT NULL,
  payment_type              TEXT                     NOT NULL,
  save_credit_card          BOOLEAN                  NOT NULL,
  status                    TEXT,
  status_check_attempts     INT8,
  status_detail             TEXT,
  PRIMARY KEY (id)
);

CREATE TABLE payxpert.payment_request_audit (
  id                        INT8 NOT NULL,
  rev                       INT4 NOT NULL,
  revtype                   INT2,
  created_at                TIMESTAMP WITH TIME ZONE,
  created_by                TEXT,
  updated_at                TIMESTAMP WITH TIME ZONE,
  updated_by                TEXT,
  amount                    NUMERIC(19, 2),
  callback_received_at      TIMESTAMP WITH TIME ZONE,
  callback_transaction_id   TEXT,
  card_brand                TEXT,
  card_expire_month         INT8,
  card_expire_year          INT8,
  card_holder_name          TEXT,
  card_is3dsecure           BOOLEAN,
  card_number               TEXT,
  client_id                 INT8,
  ctrl_callback_url         TEXT,
  ctrl_redirect_url         TEXT,
  currency                  TEXT,
  customer_redirect_url     TEXT,
  customer_token            TEXT,
  enable_recurring_payments BOOLEAN,
  error_code                TEXT,
  error_message             TEXT,
  invoice_id                INT8,
  last_status_check_at      TIMESTAMP WITH TIME ZONE,
  loan_id                   INT8,
  merchant_token            TEXT,
  operation                 TEXT,
  order_id                  TEXT,
  payment_type              TEXT,
  save_credit_card          BOOLEAN,
  status                    TEXT,
  status_check_attempts     INT8,
  status_detail             TEXT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE payxpert.rebill (
  id                            INT8                     NOT NULL,
  created_at                    TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by                    TEXT,
  entity_version                INT8                     NOT NULL,
  updated_at                    TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by                    TEXT,
  amount                        NUMERIC(19, 2)           NOT NULL,
  client_id                     INT8                     NOT NULL,
  currency                      TEXT                     NOT NULL,
  error_code                    TEXT,
  error_message                 TEXT,
  invoice_id                    INT8,
  loan_id                       INT8,
  payment_created_at            TIMESTAMP WITH TIME ZONE,
  payment_id                    INT8,
  response_statement_descriptor TEXT,
  response_transaction_id       TEXT,
  status                        TEXT,
  credit_card_id                INT8                     NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE payxpert.rebill_audit (
  id                            INT8 NOT NULL,
  rev                           INT4 NOT NULL,
  revtype                       INT2,
  created_at                    TIMESTAMP WITH TIME ZONE,
  created_by                    TEXT,
  updated_at                    TIMESTAMP WITH TIME ZONE,
  updated_by                    TEXT,
  amount                        NUMERIC(19, 2),
  client_id                     INT8,
  currency                      TEXT,
  error_code                    TEXT,
  error_message                 TEXT,
  invoice_id                    INT8,
  loan_id                       INT8,
  payment_created_at            TIMESTAMP WITH TIME ZONE,
  payment_id                    INT8,
  response_statement_descriptor TEXT,
  response_transaction_id       TEXT,
  status                        TEXT,
  credit_card_id                INT8,
  PRIMARY KEY (id, rev)
);
CREATE INDEX idx_credit_card_client_id
  ON payxpert.credit_card (client_id);
CREATE INDEX idx_payment_request_client_id
  ON payxpert.payment_request (client_id);

ALTER TABLE payxpert.payment_request
  ADD CONSTRAINT UK_eqwngvct8f0fcunblweemwjo6 UNIQUE (order_id);
CREATE INDEX idx_rebill_client_id
  ON payxpert.rebill (client_id);

ALTER TABLE payxpert.credit_card
  ADD CONSTRAINT FKm4fkytv0rvolri200toxfqu45
FOREIGN KEY (payment_request_id)
REFERENCES payxpert.payment_request;

ALTER TABLE payxpert.credit_card_audit
  ADD CONSTRAINT FK3569hucbgk44aiiloyssykgxo
FOREIGN KEY (rev)
REFERENCES common.revision;

ALTER TABLE payxpert.payment_request_audit
  ADD CONSTRAINT FKmo6s4kej82tketpuguor1nn6f
FOREIGN KEY (rev)
REFERENCES common.revision;

ALTER TABLE payxpert.rebill
  ADD CONSTRAINT FKi45wnq1kytc59dyutwnmnl7vc
FOREIGN KEY (credit_card_id)
REFERENCES payxpert.credit_card;

ALTER TABLE payxpert.rebill_audit
  ADD CONSTRAINT FKlvjb2dxbuueyp3e5824lbtffj
FOREIGN KEY (rev)
REFERENCES common.revision;
