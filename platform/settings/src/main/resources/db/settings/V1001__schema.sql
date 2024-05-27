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
-- Name: settings; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS settings;


SET search_path = settings, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: property; Type: TABLE; Schema: settings; Owner: -
--

CREATE TABLE property (
    type text NOT NULL,
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    description text,
    name text NOT NULL,
    boolean_value boolean,
    date_value date,
    date_time_value timestamp without time zone,
    decimal_value numeric(19,2),
    number_value bigint,
    text_value text
);


--
-- Name: property_audit; Type: TABLE; Schema: settings; Owner: -
--

CREATE TABLE property_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    type text NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    description text,
    name text,
    text_value text,
    number_value bigint,
    date_value date,
    boolean_value boolean,
    date_time_value timestamp without time zone,
    decimal_value numeric(19,2)
);


--
-- Name: property_audit_pkey; Type: CONSTRAINT; Schema: settings; Owner: -
--

ALTER TABLE ONLY property_audit
    ADD CONSTRAINT property_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: property_pkey; Type: CONSTRAINT; Schema: settings; Owner: -
--

ALTER TABLE ONLY property
    ADD CONSTRAINT property_pkey PRIMARY KEY (id);


--
-- Name: idx_property_name; Type: INDEX; Schema: settings; Owner: -
--

CREATE INDEX idx_property_name ON property USING btree (name);


--
-- Name: fkhpet1cbhmn6ce7cep7ge0r6o7; Type: FK CONSTRAINT; Schema: settings; Owner: -
--

ALTER TABLE ONLY property_audit
    ADD CONSTRAINT fkhpet1cbhmn6ce7cep7ge0r6o7 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

