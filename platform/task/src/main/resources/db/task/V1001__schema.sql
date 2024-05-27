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
-- Name: task; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS task;


SET search_path = task, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: agent; Type: TABLE; Schema: task; Owner: -
--

CREATE TABLE agent (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    disabled boolean NOT NULL,
    email text NOT NULL,
    task_types text
);


--
-- Name: agent_audit; Type: TABLE; Schema: task; Owner: -
--

CREATE TABLE agent_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    disabled boolean,
    email text,
    task_types text
);


--
-- Name: log; Type: TABLE; Schema: task; Owner: -
--

CREATE TABLE log (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    agent text,
    comment text,
    due_at timestamp without time zone NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    operation text NOT NULL,
    reason text,
    resolution text,
    resolution_detail text,
    resolution_sub_detail text,
    task_id bigint NOT NULL
);


--
-- Name: task; Type: TABLE; Schema: task; Owner: -
--

CREATE TABLE task (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    activity_id bigint,
    agent text,
    application_id bigint,
    assigned_at timestamp without time zone,
    client_id bigint NOT NULL,
    comment text,
    due_at timestamp without time zone NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    task_group text NOT NULL,
    loan_id bigint,
    priority bigint NOT NULL,
    resolution text,
    resolution_detail text,
    resolution_sub_detail text,
    status text NOT NULL,
    task_type text NOT NULL,
    times_postponed bigint NOT NULL,
    times_reopened bigint NOT NULL,
    workflow_id bigint
);


--
-- Name: task_audit; Type: TABLE; Schema: task; Owner: -
--

CREATE TABLE task_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    activity_id bigint,
    agent text,
    application_id bigint,
    assigned_at timestamp without time zone,
    client_id bigint,
    comment text,
    due_at timestamp without time zone,
    expires_at timestamp without time zone,
    task_group text,
    loan_id bigint,
    priority bigint,
    resolution text,
    resolution_detail text,
    resolution_sub_detail text,
    status text,
    task_type text,
    times_postponed bigint,
    times_reopened bigint,
    workflow_id bigint
);


--
-- Name: agent_audit_pkey; Type: CONSTRAINT; Schema: task; Owner: -
--

ALTER TABLE ONLY agent_audit
    ADD CONSTRAINT agent_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: agent_pkey; Type: CONSTRAINT; Schema: task; Owner: -
--

ALTER TABLE ONLY agent
    ADD CONSTRAINT agent_pkey PRIMARY KEY (id);


--
-- Name: log_pkey; Type: CONSTRAINT; Schema: task; Owner: -
--

ALTER TABLE ONLY log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- Name: task_audit_pkey; Type: CONSTRAINT; Schema: task; Owner: -
--

ALTER TABLE ONLY task_audit
    ADD CONSTRAINT task_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: task_pkey; Type: CONSTRAINT; Schema: task; Owner: -
--

ALTER TABLE ONLY task
    ADD CONSTRAINT task_pkey PRIMARY KEY (id);


--
-- Name: uk_pxogqxl64ae07j2lox1tgvrlx; Type: CONSTRAINT; Schema: task; Owner: -
--

ALTER TABLE ONLY agent
    ADD CONSTRAINT uk_pxogqxl64ae07j2lox1tgvrlx UNIQUE (email);


--
-- Name: idx_task_activity_id; Type: INDEX; Schema: task; Owner: -
--

CREATE INDEX idx_task_activity_id ON task USING btree (activity_id);


--
-- Name: idx_task_application_id; Type: INDEX; Schema: task; Owner: -
--

CREATE INDEX idx_task_application_id ON task USING btree (application_id);


--
-- Name: idx_task_client_id; Type: INDEX; Schema: task; Owner: -
--

CREATE INDEX idx_task_client_id ON task USING btree (client_id);


--
-- Name: idx_task_due_at; Type: INDEX; Schema: task; Owner: -
--

CREATE INDEX idx_task_due_at ON task USING btree (due_at);


--
-- Name: idx_task_expires_at; Type: INDEX; Schema: task; Owner: -
--

CREATE INDEX idx_task_expires_at ON task USING btree (expires_at);


--
-- Name: idx_task_loan_id; Type: INDEX; Schema: task; Owner: -
--

CREATE INDEX idx_task_loan_id ON task USING btree (loan_id);


--
-- Name: idx_task_workflow_id; Type: INDEX; Schema: task; Owner: -
--

CREATE INDEX idx_task_workflow_id ON task USING btree (workflow_id);


--
-- Name: fk2ikr09ebywpggoq8v8wylj62n; Type: FK CONSTRAINT; Schema: task; Owner: -
--

ALTER TABLE ONLY agent_audit
    ADD CONSTRAINT fk2ikr09ebywpggoq8v8wylj62n FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkno9c9thtevximrgpl6uqkp91n; Type: FK CONSTRAINT; Schema: task; Owner: -
--

ALTER TABLE ONLY task_audit
    ADD CONSTRAINT fkno9c9thtevximrgpl6uqkp91n FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

ALTER TABLE task.log ADD CONSTRAINT fk_log_task_id FOREIGN KEY (task_id) REFERENCES task.task (id);
CREATE INDEX IF NOT EXISTS idx_log_task_id ON task.log USING btree (task_id);
