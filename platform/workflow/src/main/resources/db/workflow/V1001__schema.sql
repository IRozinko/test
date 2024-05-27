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
-- Name: workflow; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS workflow;


SET search_path = workflow, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: activity; Type: TABLE; Schema: workflow; Owner: -
--

CREATE TABLE activity (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    actor text NOT NULL,
    attempts bigint NOT NULL,
    completed_at timestamp without time zone,
    error text,
    expires_at timestamp without time zone,
    name text NOT NULL,
    next_attempt_at timestamp without time zone,
    resolution text,
    resolution_detail text,
    status text NOT NULL,
    workflow_id bigint NOT NULL
);


--
-- Name: activity_audit; Type: TABLE; Schema: workflow; Owner: -
--

CREATE TABLE activity_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    actor text,
    attempts bigint,
    completed_at timestamp without time zone,
    error text,
    expires_at timestamp without time zone,
    name text,
    next_attempt_at timestamp without time zone,
    resolution text,
    resolution_detail text,
    status text,
    workflow_id bigint
);


--
-- Name: workflow; Type: TABLE; Schema: workflow; Owner: -
--

CREATE TABLE workflow (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    client_id bigint NOT NULL,
    completed_at timestamp without time zone,
    loan_id bigint,
    name text NOT NULL,
    status text NOT NULL,
    terminate_reason text
);


--
-- Name: workflow_attribute; Type: TABLE; Schema: workflow; Owner: -
--

CREATE TABLE workflow_attribute (
    workflow_id bigint NOT NULL,
    value text,
    key text NOT NULL
);


--
-- Name: workflow_attribute_audit; Type: TABLE; Schema: workflow; Owner: -
--

CREATE TABLE workflow_attribute_audit (
    rev integer NOT NULL,
    workflow_id bigint NOT NULL,
    value text NOT NULL,
    key text NOT NULL,
    revtype smallint
);


--
-- Name: workflow_audit; Type: TABLE; Schema: workflow; Owner: -
--

CREATE TABLE workflow_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    application_id bigint,
    client_id bigint,
    completed_at timestamp without time zone,
    loan_id bigint,
    name text,
    status text,
    terminate_reason text
);


--
-- Name: activity_audit_pkey; Type: CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY activity_audit
    ADD CONSTRAINT activity_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: activity_pkey; Type: CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT activity_pkey PRIMARY KEY (id);


--
-- Name: idx_activity_activity_name_uq; Type: CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY activity
    ADD CONSTRAINT idx_activity_activity_name_uq UNIQUE (workflow_id, name);


--
-- Name: workflow_attribute_audit_pkey; Type: CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY workflow_attribute_audit
    ADD CONSTRAINT workflow_attribute_audit_pkey PRIMARY KEY (rev, workflow_id, value, key);


--
-- Name: workflow_attribute_pkey; Type: CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY workflow_attribute
    ADD CONSTRAINT workflow_attribute_pkey PRIMARY KEY (workflow_id, key);


--
-- Name: workflow_audit_pkey; Type: CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY workflow_audit
    ADD CONSTRAINT workflow_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: workflow_pkey; Type: CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY workflow
    ADD CONSTRAINT workflow_pkey PRIMARY KEY (id);


--
-- Name: idx_activity_expires_at; Type: INDEX; Schema: workflow; Owner: -
--

CREATE INDEX idx_activity_expires_at ON activity USING btree (expires_at, status);


--
-- Name: idx_activity_next_attempt_at; Type: INDEX; Schema: workflow; Owner: -
--

CREATE INDEX idx_activity_next_attempt_at ON activity USING btree (next_attempt_at, status, actor);


--
-- Name: idx_activity_workflow_id; Type: INDEX; Schema: workflow; Owner: -
--

CREATE INDEX idx_activity_workflow_id ON activity USING btree (workflow_id);


--
-- Name: idx_workflow_application_id; Type: INDEX; Schema: workflow; Owner: -
--

CREATE INDEX idx_workflow_application_id ON workflow USING btree (application_id);


--
-- Name: idx_workflow_client_id; Type: INDEX; Schema: workflow; Owner: -
--

CREATE INDEX idx_workflow_client_id ON workflow USING btree (client_id);


--
-- Name: idx_workflow_loan_id; Type: INDEX; Schema: workflow; Owner: -
--

CREATE INDEX idx_workflow_loan_id ON workflow USING btree (loan_id);


--
-- Name: fkercl063c9xd4ylsrtii3ck22f; Type: FK CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY activity_audit
    ADD CONSTRAINT fkercl063c9xd4ylsrtii3ck22f FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fki8m6lkqy9tj810qu2pq7b0oaw; Type: FK CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY workflow_attribute_audit
    ADD CONSTRAINT fki8m6lkqy9tj810qu2pq7b0oaw FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkpb5swgfxjcq8eyhv5b7h5nblf; Type: FK CONSTRAINT; Schema: workflow; Owner: -
--

ALTER TABLE ONLY workflow_audit
    ADD CONSTRAINT fkpb5swgfxjcq8eyhv5b7h5nblf FOREIGN KEY (rev) REFERENCES common.revision(id);


ALTER TABLE workflow.workflow_attribute ADD CONSTRAINT fk_workflow_attribute_workflow_id FOREIGN KEY (workflow_id) REFERENCES workflow.workflow (id);
CREATE INDEX IF NOT EXISTS idx_workflow_attribute_workflow_id ON workflow.workflow_attribute USING btree (workflow_id);
ALTER TABLE workflow.activity ADD CONSTRAINT fk_activity_workflow_id FOREIGN KEY (workflow_id) REFERENCES workflow.workflow (id);
CREATE INDEX IF NOT EXISTS idx_activity_workflow_id ON workflow.activity USING btree (workflow_id);


