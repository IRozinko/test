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
-- Name: affiliate; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS affiliate;


SET search_path = affiliate, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: event; Type: TABLE; Schema: affiliate; Owner: -
--

CREATE TABLE event (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    client_id bigint NOT NULL,
    event_type text NOT NULL,
    loan_id bigint,
    next_report_attempt_at timestamp without time zone,
    report_error text,
    report_retry_attempts integer NOT NULL,
    report_status text NOT NULL,
    report_url text,
    reported_at timestamp without time zone,
    lead_id bigint NOT NULL,
    partner_id bigint
);


--
-- Name: event_audit; Type: TABLE; Schema: affiliate; Owner: -
--

CREATE TABLE event_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    application_id bigint,
    client_id bigint,
    event_type text,
    loan_id bigint,
    next_report_attempt_at timestamp without time zone,
    report_error text,
    report_retry_attempts integer,
    report_status text,
    report_url text,
    reported_at timestamp without time zone,
    lead_id bigint,
    partner_id bigint
);


--
-- Name: lead; Type: TABLE; Schema: affiliate; Owner: -
--

CREATE TABLE lead (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    affiliate_lead_id text,
    affiliate_name text,
    application_id bigint,
    campaign text,
    client_id bigint NOT NULL,
    sub_affiliate_lead_id1 text,
    sub_affiliate_lead_id2 text,
    sub_affiliate_lead_id3 text,
    unknown_partner boolean NOT NULL,
    partner_id bigint
);


--
-- Name: lead_audit; Type: TABLE; Schema: affiliate; Owner: -
--

CREATE TABLE lead_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    affiliate_lead_id text,
    affiliate_name text,
    application_id bigint,
    campaign text,
    client_id bigint,
    sub_affiliate_lead_id1 text,
    sub_affiliate_lead_id2 text,
    sub_affiliate_lead_id3 text,
    unknown_partner boolean,
    partner_id bigint
);


--
-- Name: partner; Type: TABLE; Schema: affiliate; Owner: -
--

CREATE TABLE partner (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    action_report_url text,
    active boolean NOT NULL,
    lead_condition_workflow_activity_name text,
    lead_condition_workflow_activity_resolution text,
    lead_report_url text,
    name text NOT NULL
);


--
-- Name: partner_audit; Type: TABLE; Schema: affiliate; Owner: -
--

CREATE TABLE partner_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    action_report_url text,
    active boolean,
    lead_condition_workflow_activity_name text,
    lead_condition_workflow_activity_resolution text,
    lead_report_url text,
    name text
);


--
-- Name: event_audit_pkey; Type: CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY event_audit
    ADD CONSTRAINT event_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: event_pkey; Type: CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY event
    ADD CONSTRAINT event_pkey PRIMARY KEY (id);


--
-- Name: lead_audit_pkey; Type: CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY lead_audit
    ADD CONSTRAINT lead_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: lead_pkey; Type: CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY lead
    ADD CONSTRAINT lead_pkey PRIMARY KEY (id);


--
-- Name: partner_audit_pkey; Type: CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY partner_audit
    ADD CONSTRAINT partner_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: partner_pkey; Type: CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT partner_pkey PRIMARY KEY (id);


--
-- Name: uk_a7jsvq2r4k841xht6cos347uc; Type: CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY partner
    ADD CONSTRAINT uk_a7jsvq2r4k841xht6cos347uc UNIQUE (name);


--
-- Name: idx_event_client_id; Type: INDEX; Schema: affiliate; Owner: -
--

CREATE INDEX idx_event_client_id ON event USING btree (client_id);


--
-- Name: idx_event_lead_id; Type: INDEX; Schema: affiliate; Owner: -
--

CREATE INDEX idx_event_lead_id ON event USING btree (lead_id);


--
-- Name: idx_event_next_report_attempt_at; Type: INDEX; Schema: affiliate; Owner: -
--

CREATE INDEX idx_event_next_report_attempt_at ON event USING btree (next_report_attempt_at);


--
-- Name: idx_event_partner_id; Type: INDEX; Schema: affiliate; Owner: -
--

CREATE INDEX idx_event_partner_id ON event USING btree (partner_id);


--
-- Name: idx_lead_client_id; Type: INDEX; Schema: affiliate; Owner: -
--

CREATE INDEX idx_lead_client_id ON lead USING btree (client_id);


--
-- Name: idx_lead_partner_id; Type: INDEX; Schema: affiliate; Owner: -
--

CREATE INDEX idx_lead_partner_id ON lead USING btree (partner_id);


--
-- Name: fkbl950seta0yhshf8p0p0ov61t; Type: FK CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY event_audit
    ADD CONSTRAINT fkbl950seta0yhshf8p0p0ov61t FOREIGN KEY (rev) REFERENCES common.revision(id);

--
-- Name: fki7i1w2qomvb2rlovlr2naf0o7; Type: FK CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY partner_audit
    ADD CONSTRAINT fki7i1w2qomvb2rlovlr2naf0o7 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkn71qehi9j18gskycjwwtydepx; Type: FK CONSTRAINT; Schema: affiliate; Owner: -
--

ALTER TABLE ONLY lead_audit
    ADD CONSTRAINT fkn71qehi9j18gskycjwwtydepx FOREIGN KEY (rev) REFERENCES common.revision(id);



--
-- PostgreSQL database dump complete
--

ALTER TABLE affiliate.lead ADD CONSTRAINT fk_lead_partner_id FOREIGN KEY (partner_id) REFERENCES affiliate.partner (id);
CREATE INDEX IF NOT EXISTS idx_lead_partner_id ON affiliate.lead USING btree (partner_id);

ALTER TABLE affiliate.event ADD CONSTRAINT fk_event_partner_id FOREIGN KEY (partner_id) REFERENCES affiliate.partner (id);
CREATE INDEX IF NOT EXISTS idx_event_partner_id ON affiliate.event USING btree (partner_id);

ALTER TABLE affiliate.event ADD CONSTRAINT fk_event_lead_id FOREIGN KEY (lead_id) REFERENCES affiliate.lead (id);
CREATE INDEX IF NOT EXISTS idx_event_lead_id ON affiliate.event USING btree (lead_id);
