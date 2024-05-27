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
-- Name: spain_scoring; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS spain_scoring;


SET search_path = spain_scoring, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: log; Type: TABLE; Schema: spain_scoring; Owner: -
--

CREATE TABLE log (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    client_id bigint,
    error text,
    loan_id bigint,
    request_attributes text,
    response_body text,
    response_status_code integer NOT NULL,
    score double precision NOT NULL,
    status text NOT NULL,
    type text NOT NULL
);


--
-- Name: log_pkey; Type: CONSTRAINT; Schema: spain_scoring; Owner: -
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: idx_log_client_id; Type: INDEX; Schema: spain_scoring; Owner: -
--

CREATE INDEX idx_log_client_id ON log USING btree (client_id);


--
-- PostgreSQL database dump complete
--

