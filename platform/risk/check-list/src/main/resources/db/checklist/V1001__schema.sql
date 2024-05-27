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
-- Name: checklist; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS checklist;


SET search_path = checklist, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: checklist_entry; Type: TABLE; Schema: checklist; Owner: -
--

CREATE TABLE checklist_entry (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    value1 text NOT NULL,
    type text NOT NULL
);


--
-- Name: checklist_entry_audit; Type: TABLE; Schema: checklist; Owner: -
--

CREATE TABLE checklist_entry_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    value1 text,
    type bigint
);


--
-- Name: checklist_type; Type: TABLE; Schema: checklist; Owner: -
--

CREATE TABLE checklist_type (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    action text NOT NULL,
    type text NOT NULL
);


--
-- Name: checklist_type_audit; Type: TABLE; Schema: checklist; Owner: -
--

CREATE TABLE checklist_type_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    action text,
    type text
);


--
-- Name: checklist_entry_audit_pkey; Type: CONSTRAINT; Schema: checklist; Owner: -
--

ALTER TABLE ONLY checklist_entry_audit
    ADD CONSTRAINT checklist_entry_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: checklist_entry_pkey; Type: CONSTRAINT; Schema: checklist; Owner: -
--

ALTER TABLE ONLY checklist_entry
    ADD CONSTRAINT checklist_entry_pkey PRIMARY KEY (id);


--
-- Name: checklist_type_audit_pkey; Type: CONSTRAINT; Schema: checklist; Owner: -
--

ALTER TABLE ONLY checklist_type_audit
    ADD CONSTRAINT checklist_type_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: checklist_type_pkey; Type: CONSTRAINT; Schema: checklist; Owner: -
--

ALTER TABLE ONLY checklist_type
    ADD CONSTRAINT checklist_type_pkey PRIMARY KEY (id);


--
-- Name: uk_pbq37jovpo3rrw1sgsnoeiw1y; Type: CONSTRAINT; Schema: checklist; Owner: -
--

ALTER TABLE ONLY checklist_type
    ADD CONSTRAINT uk_pbq37jovpo3rrw1sgsnoeiw1y UNIQUE (type);


--
-- Name: idx_checklist_entry_type; Type: INDEX; Schema: checklist; Owner: -
--

CREATE INDEX idx_checklist_entry_type ON checklist_entry USING btree (type);


--
-- Name: fk7q094jf2nfgcddgjqd3wac0aa; Type: FK CONSTRAINT; Schema: checklist; Owner: -
--

ALTER TABLE ONLY checklist_entry
    ADD CONSTRAINT fk7q094jf2nfgcddgjqd3wac0aa FOREIGN KEY (type) REFERENCES checklist_type(type);


--
-- Name: fkdky7q4oahyesvuckrufuvpaf0; Type: FK CONSTRAINT; Schema: checklist; Owner: -
--

ALTER TABLE ONLY checklist_entry_audit
    ADD CONSTRAINT fkdky7q4oahyesvuckrufuvpaf0 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkiulslgo9gm9edh5m2wk4hig0o; Type: FK CONSTRAINT; Schema: checklist; Owner: -
--

ALTER TABLE ONLY checklist_type_audit
    ADD CONSTRAINT fkiulslgo9gm9edh5m2wk4hig0o FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

