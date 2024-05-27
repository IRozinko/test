CREATE TABLE sms.incoming (
  id             INT8      NOT NULL,
  created_at     TIMESTAMP NOT NULL,
  created_by     TEXT,
  entity_version INT8      NOT NULL,
  updated_at     TIMESTAMP NOT NULL,
  updated_by     TEXT,
  phone_number   TEXT,
  raw_data_json  TEXT,
  source         TEXT,
  text           TEXT,
  PRIMARY KEY (id)
);
