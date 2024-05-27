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
-- Name: spain_inglobaly; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS spain_inglobaly;


SET search_path = spain_inglobaly, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: response; Type: TABLE; Schema: spain_inglobaly; Owner: -
--

CREATE TABLE response (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    client_id bigint,
    date_of_birth date,
    error text,
    first_name text,
    last_name text,
    requested_document_number text,
    response_body text,
    second_last_name text,
    status text NOT NULL
);


--
-- Name: response_pkey; Type: CONSTRAINT; Schema: spain_inglobaly; Owner: -
--

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (id);


--
-- Name: idx_response_client_id; Type: INDEX; Schema: spain_inglobaly; Owner: -
--

CREATE INDEX idx_response_client_id ON response USING btree (client_id);


--
-- PostgreSQL database dump complete
--

