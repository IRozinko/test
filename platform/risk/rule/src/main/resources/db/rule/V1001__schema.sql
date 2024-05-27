--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.5
-- Dumped by pg_dump version 9.5.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: rule; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS rule;


SET search_path = rule, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: rule_log; Type: TABLE; Schema: rule; Owner: -
--

CREATE TABLE rule_log (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    checks_json text,
    client_id bigint NOT NULL,
    decision text NOT NULL,
    loan_id bigint,
    reason text,
    reason_details text,
    rule text NOT NULL,
    rule_set_result_id bigint NOT NULL
);


--
-- Name: rule_set_log; Type: TABLE; Schema: rule; Owner: -
--

CREATE TABLE rule_set_log (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    client_id bigint NOT NULL,
    decision text NOT NULL,
    executed_at timestamp without time zone NOT NULL,
    loan_id bigint,
    reject_reason text,
    reject_reason_details text,
    rule_set text NOT NULL
);


--
-- Name: rule_log_pkey; Type: CONSTRAINT; Schema: rule; Owner: -
--

ALTER TABLE ONLY rule_log
    ADD CONSTRAINT rule_log_pkey PRIMARY KEY (id);


--
-- Name: rule_set_log_pkey; Type: CONSTRAINT; Schema: rule; Owner: -
--

ALTER TABLE ONLY rule_set_log
    ADD CONSTRAINT rule_set_log_pkey PRIMARY KEY (id);


--
-- Name: idx_rule_log_client_id; Type: INDEX; Schema: rule; Owner: -
--

CREATE INDEX idx_rule_log_client_id ON rule_log USING btree (client_id);


--
-- Name: idx_rule_set_log_client_id; Type: INDEX; Schema: rule; Owner: -
--

CREATE INDEX idx_rule_set_log_client_id ON rule_set_log USING btree (client_id);


--
-- PostgreSQL database dump complete
--

ALTER TABLE rule.rule_log ADD CONSTRAINT fk_rule_log_rule_set_result_id FOREIGN KEY (rule_set_result_id) REFERENCES rule.rule_set_log (id);
CREATE INDEX IF NOT EXISTS idx_rule_log_rule_set_result_id ON rule.rule_log USING btree (rule_set_result_id);
