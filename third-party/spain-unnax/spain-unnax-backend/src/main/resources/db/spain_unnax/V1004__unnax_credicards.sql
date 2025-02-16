CREATE TABLE credit_card (
  id                         INT8                     NOT NULL,
  created_at                 TIMESTAMP with TIME ZONE NOT NULL,
  created_by                 TEXT,
  entity_version             INT8                     NOT NULL,
  updated_at                 TIMESTAMP with TIME ZONE NOT NULL,
  updated_by                 TEXT,
  active                     BOOLEAN                  NOT NULL,
  callback_transaction_id    TEXT                     NOT NULL,
  card_brand                 TEXT,
  card_bank                  TEXT,
  card_expire_month          INT8,
  card_expire_year           INT8,
  card_holder_name           TEXT,
  card_token                 TEXT,
  order_code                 text                     NOT NULL,
  status                     text,
  error_details              text,
  client_id                  INT8                     NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE credit_card_audit (
  id                         INT8 NOT NULL,
  rev                        INT4 NOT NULL,
  revtype                    INT2,
  created_at                 TIMESTAMP with TIME ZONE,
  created_by                 TEXT,
  updated_at                 TIMESTAMP with TIME ZONE,
  updated_by                 TEXT,
  active                     BOOLEAN,
  callback_transaction_id    TEXT,
  card_bank                  TEXT,
  card_brand                 TEXT,
  card_expire_month          INT8,
  card_expire_year           INT8,
  order_code                 text                     NOT NULL,
  card_holder_name           TEXT,
  card_token                 TEXT,
  status                     text,
  error_details              text,
  client_id                  INT8,
  PRIMARY KEY (id, rev)
);
