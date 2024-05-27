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
-- Name: storage; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS storage;


SET search_path = storage, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: cloud_file; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE cloud_file (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    content_type text NOT NULL,
    directory text NOT NULL,
    file_size bigint NOT NULL,
    file_uuid text NOT NULL,
    last_downloaded_at timestamp without time zone,
    original_file_name text NOT NULL,
    times_downloaded bigint NOT NULL
);


--
-- Name: cloud_file_audit; Type: TABLE; Schema: storage; Owner: -
--

CREATE TABLE cloud_file_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    content_type text,
    directory text,
    file_size bigint,
    file_uuid text,
    last_downloaded_at timestamp without time zone,
    original_file_name text,
    times_downloaded bigint
);


--
-- Name: cloud_file_audit_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY cloud_file_audit
    ADD CONSTRAINT cloud_file_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: cloud_file_pkey; Type: CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY cloud_file
    ADD CONSTRAINT cloud_file_pkey PRIMARY KEY (id);


--
-- Name: fkhho9xj77lxapdywee552l1r07; Type: FK CONSTRAINT; Schema: storage; Owner: -
--

ALTER TABLE ONLY cloud_file_audit
    ADD CONSTRAINT fkhho9xj77lxapdywee552l1r07 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

