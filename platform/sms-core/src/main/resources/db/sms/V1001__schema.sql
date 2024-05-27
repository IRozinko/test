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
-- Name: sms; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS sms;


SET search_path = sms, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: log; Type: TABLE; Schema: sms; Owner: -
--

CREATE TABLE log (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    attempt_timeout_in_seconds integer NOT NULL,
    attempts integer NOT NULL,
    client_id bigint,
    cms_key text,
    delivery_report_error text,
    delivery_report_received_at timestamp without time zone,
    delivery_report_status text,
    delivery_report_status2 text,
    error text,
    max_attempts integer NOT NULL,
    next_attempt_at timestamp without time zone NOT NULL,
    provider text,
    provider_id text,
    provider_message text,
    sender_id text NOT NULL,
    sending_status text NOT NULL,
    sms_text text NOT NULL,
    send_to text NOT NULL
);


--
-- Name: log_pkey; Type: CONSTRAINT; Schema: sms; Owner: -
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: idx_log_client_id; Type: INDEX; Schema: sms; Owner: -
--

CREATE INDEX idx_log_client_id ON log USING btree (client_id);


--
-- Name: idx_log_next_attempt_at; Type: INDEX; Schema: sms; Owner: -
--

CREATE INDEX idx_log_next_attempt_at ON log USING btree (next_attempt_at);


--
-- PostgreSQL database dump complete
--

