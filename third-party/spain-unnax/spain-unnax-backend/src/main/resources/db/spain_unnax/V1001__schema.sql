SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = spain_unnax, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TABLE webhook
(
  id                 bigint                      NOT NULL PRIMARY KEY,
  created_at         timestamp without time zone NOT NULL,
  created_by         text,
  entity_version     bigint                      NOT NULL,
  updated_at         timestamp without time zone NOT NULL,
  updated_by         text,

  event              text                        NOT NULL,
  external_id        bigint                      NOT NULL,
  type               text                        NOT NULL,
  state              integer,
  target             text                        NOT NULL,
  webhook_created_at timestamp without time zone,
  webhook_updated_at timestamp without time zone
);

CREATE TABLE callback
(
  id               bigint                      NOT NULL PRIMARY KEY,
  created_at       timestamp without time zone NOT NULL,
  created_by       text,
  entity_version   bigint                      NOT NULL,
  updated_at       timestamp without time zone NOT NULL,
  updated_by       text,

  data             text,
  event            text,
  date             timestamp without time zone,
  signature        text,
  response_id      text,
  trace_identifier text,
  environment      text
);


CREATE TABLE transfer_auto
(
  id                  bigint                      NOT NULL PRIMARY KEY,
  created_at          timestamp without time zone NOT NULL,
  created_by          text,
  entity_version      bigint                      NOT NULL,
  updated_at          timestamp without time zone NOT NULL,
  updated_by          text,

  amount              integer                     NOT NULL,
  destination_account text                        NOT NULL,
  customer_code       text                        NOT NULL,
  order_code          text                        NOT NULL UNIQUE,

  currency            text,
  customer_names      text,
  concept             text,
  bank_order_code     text,
  transfer_type       text,
  tags                text,
  status              text,
  error_details       text,
  source_account      text,

  order_pending_at    timestamp without time zone,
  order_created_at    timestamp without time zone,
  order_processed_at  timestamp without time zone
);

CREATE TABLE transfer_auto_audit
(
  id                  bigint                      NOT NULL,
  rev                 INT4                        NOT NULL,
  revtype             INT2,
  created_at          timestamp without time zone NOT NULL,
  created_by          text,
  entity_version      bigint                      NOT NULL,
  updated_at          timestamp without time zone NOT NULL,
  updated_by          text,

  amount              integer                     NOT NULL,
  destination_account text                        NOT NULL,
  customer_code       text                        NOT NULL,
  order_code          text                        NOT NULL,

  currency            text,
  customer_names      text,
  concept             text,
  bank_order_code     text,
  transfer_type       text,
  tags                text,
  status              text,
  PRIMARY KEY (id, rev)
);
