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
-- Name: cms; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS cms;


SET search_path = cms, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: item; Type: TABLE; Schema: cms; Owner: -
--

CREATE TABLE item (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    content_template text,
    description text,
    email_body_template text,
    email_subject_template text,
    item_type text NOT NULL,
    item_key text NOT NULL,
    scope text NOT NULL,
    sms_text_template text,
    title_template text
);


--
-- Name: item_audit; Type: TABLE; Schema: cms; Owner: -
--

CREATE TABLE item_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    content_template text,
    description text,
    email_body_template text,
    email_subject_template text,
    item_type text,
    item_key text,
    scope text,
    sms_text_template text,
    title_template text
);


--
-- Name: item_audit_pkey; Type: CONSTRAINT; Schema: cms; Owner: -
--

ALTER TABLE ONLY item_audit
    ADD CONSTRAINT item_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: item_pkey; Type: CONSTRAINT; Schema: cms; Owner: -
--

ALTER TABLE ONLY item
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- Name: uk_s5n165pbuviwifg8owig8v41u; Type: CONSTRAINT; Schema: cms; Owner: -
--

ALTER TABLE ONLY item
    ADD CONSTRAINT uk_s5n165pbuviwifg8owig8v41u UNIQUE (item_key);


--
-- Name: fkittjc909fo5u7wfix4q4h1eeg; Type: FK CONSTRAINT; Schema: cms; Owner: -
--

ALTER TABLE ONLY item_audit
    ADD CONSTRAINT fkittjc909fo5u7wfix4q4h1eeg FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

