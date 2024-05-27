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
-- Name: email; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS email;


SET search_path = email, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: log; Type: TABLE; Schema: email; Owner: -
--

CREATE TABLE log (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    attachment_file_ids text,
    attempt_timeout_in_seconds integer NOT NULL,
    attempts integer NOT NULL,
    body text NOT NULL,
    client_id bigint,
    cms_key text,
    error text,
    send_from text NOT NULL,
    max_attempts integer NOT NULL,
    next_attempt_at timestamp without time zone NOT NULL,
    provider text,
    provider_id text,
    provider_message text,
    sending_status text NOT NULL,
    subject text NOT NULL,
    send_to text NOT NULL
);


--
-- Name: log_pkey; Type: CONSTRAINT; Schema: email; Owner: -
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: idx_log_next_attempt_at; Type: INDEX; Schema: email; Owner: -
--

CREATE INDEX idx_log_next_attempt_at ON log USING btree (next_attempt_at);


--
-- PostgreSQL database dump complete
--

