set search_path to crm;

CREATE TABLE country (
  id                       BIGSERIAL NOT NULL,
  country                  TEXT      NOT NULL,
  country_display_name     TEXT      NOT NULL,
  nationality              TEXT      NOT NULL,
  nationality_display_name TEXT      NOT NULL,
  PRIMARY KEY (id)
);

