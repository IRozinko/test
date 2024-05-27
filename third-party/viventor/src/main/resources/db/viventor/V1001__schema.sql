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
-- Name: viventor; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS viventor;


SET search_path = viventor, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: log; Type: TABLE; Schema: viventor; Owner: -
--

CREATE TABLE log (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,

    loan_id bigint NOT NULL,
    viventor_loan_id text NOT NULL,
    request_type text NOT NULL,
    request_url text NOT NULL,
    request_body text,
    response_body text,
    response_status_code integer NOT NULL,
    status text NOT NULL
);


--
-- Name: log_pkey; Type: CONSTRAINT; Schema: viventor; Owner: -
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: idx_log_client_id; Type: INDEX; Schema: viventor; Owner: -
--

CREATE INDEX idx_viventor_log_loan_id ON log USING btree (loan_id);

CREATE INDEX idx_viventor_log_viventor_loan_id ON log USING btree (viventor_loan_id);


--
-- PostgreSQL database dump complete
--

