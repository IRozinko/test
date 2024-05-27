CREATE TABLE dowjones.request (
  id                         INT8                     NOT NULL,
  created_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by                 TEXT,
  entity_version             INT8                     NOT NULL,
  updated_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by                 TEXT,
  request_body               TEXT,
  response_body              TEXT,
  request_url                TEXT,
  response_status_code       INTEGER                  NOT NULL,
  client_id                  BIGINT                   NOT NULL,
  status                     TEXT,
  reason                     TEXT,
  error                      TEXT,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_dowjones_client_id
    ON dowjones.request USING btree (client_id);

CREATE TABLE dowjones.request_audit (
  id                         INT8 NOT NULL,
  rev                        INT4 NOT NULL,
  revtype                    INT2,
  created_at                 TIMESTAMP WITH TIME ZONE,
  created_by                 TEXT,
  updated_at                 TIMESTAMP WITH TIME ZONE,
  updated_by                 TEXT,
  request_url                TEXT,
  request_body               TEXT,
  response_body              TEXT,
  response_status_code       INTEGER,
  client_id                  BIGINT,
  status                     TEXT,
  reason                     TEXT,
  error                      TEXT,
  PRIMARY KEY (id, rev)
);

CREATE TABLE dowjones.search_result (
  id                         INT8                     NOT NULL,
  created_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by                 TEXT,
  entity_version             INT8                     NOT NULL,
  updated_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by                 TEXT,
  client_id                  BIGINT                   NOT NULL,
  total_hits                 INT,
  hits_from                  INT,
  hits_to                    INT,
  truncated                  BOOLEAN                  NOT NULL,
  cached_results_id          TEXT,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_search_result_client_id
    ON dowjones.search_result USING btree (client_id);

CREATE TABLE dowjones.search_result_audit (
  id                         INT8 NOT NULL,
  rev                        INT4 NOT NULL,
  revtype                    INT2,
  created_at                 TIMESTAMP WITH TIME ZONE,
  created_by                 TEXT,
  updated_at                 TIMESTAMP WITH TIME ZONE,
  updated_by                 TEXT,
  client_id                  BIGINT,
  total_hits                 INT,
  hits_from                  INT,
  hits_to                    INT,
  truncated                  BOOLEAN,
  cached_results_id          TEXT,
  PRIMARY KEY (id, rev)
);


CREATE TABLE dowjones.match (
  id                         INT8                     NOT NULL,
  created_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by                 TEXT,
  entity_version             INT8                     NOT NULL,
  updated_at                 TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by                 TEXT,
  search_result_id           BIGINT                   NOT NULL,
  score                      TEXT,
  risk_indicator             TEXT,
  primary_name               TEXT,
  country_code               TEXT,
  gender                     TEXT,
  date_of_birth_year         INT,
  date_of_birth_month        INT,
  date_of_birth_day          INT,
  first_name                 TEXT,
  last_name                  TEXT,
  second_last_name           TEXT,
  second_first_name          TEXT,
  maiden_name                TEXT,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_match_search_result_id
    ON dowjones.match USING btree (search_result_id);

CREATE TABLE dowjones.match_audit (
  id                         INT8 NOT NULL,
  rev                        INT4 NOT NULL,
  revtype                    INT2,
  created_at                 TIMESTAMP WITH TIME ZONE,
  created_by                 TEXT,
  updated_at                 TIMESTAMP WITH TIME ZONE,
  updated_by                 TEXT,
  search_result_id           BIGINT,
  risk_indicator             TEXT,
  primary_name               TEXT,
  country_code               TEXT,
  gender                     TEXT,
  score                      TEXT,
  date_of_birth_year         INT,
  date_of_birth_month        INT,
  date_of_birth_day          INT,
  first_name                 TEXT,
  last_name                  TEXT,
  second_last_name           TEXT,
  second_first_name          TEXT,
  maiden_name                TEXT,
  PRIMARY KEY (id, rev)
);
