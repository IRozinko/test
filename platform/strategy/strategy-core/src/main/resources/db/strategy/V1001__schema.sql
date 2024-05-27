SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;


CREATE SCHEMA IF NOT EXISTS strategy;

SET search_path = strategy, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;


CREATE TABLE calculation_strategy (
    id               BIGINT                      NOT NULL,
    entity_version   BIGINT                      NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by       TEXT,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_by       TEXT,

    strategy_type    TEXT                        NOT NULL,
    calculation_type TEXT                        NOT NULL,
    version          TEXT                        NOT NULL,
    enabled          BOOLEAN                     NOT NULL DEFAULT FALSE,
    is_default       BOOLEAN CHECK (is_default IS TRUE OR is_default IS NULL),
    PRIMARY KEY (id)
);

create unique index if not exists uq_default_calculation_strategy
    on calculation_strategy (strategy_type, is_default);

create unique index if not exists uq_calculation_strategy_name
    on calculation_strategy (strategy_type, calculation_type, version);

CREATE TABLE calculation_strategy_audit (
    id               BIGINT  NOT NULL,
    rev              INTEGER NOT NULL,
    revtype          SMALLINT,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    created_by       TEXT,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    updated_by       TEXT,

    strategy_type    TEXT,
    calculation_type TEXT,
    version          TEXT,
    enabled          BOOLEAN,
    is_default       BOOLEAN
);
