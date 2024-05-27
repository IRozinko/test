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
-- Name: instantor; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS instantor;


SET search_path = instantor, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: response; Type: TABLE; Schema: instantor; Owner: -
--

CREATE TABLE response (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    account_numbers text,
    client_id bigint,
    error text,
    latest boolean NOT NULL,
    name_for_verification text,
    param_action text,
    param_encryption text,
    param_hash text,
    param_message_id text,
    param_payload text,
    param_source text,
    param_timestamp text,
    payload_json text,
    personal_number_for_verification text,
    status text NOT NULL
);


--
-- Name: transaction; Type: TABLE; Schema: instantor; Owner: -
--

CREATE TABLE transaction (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    account_holder_name text NOT NULL,
    account_number text NOT NULL,
    amount numeric(19,2) NOT NULL,
    balance numeric(19,2),
    category text,
    client_id bigint,
    currency text NOT NULL,
    date date NOT NULL,
    description text,
    response_id bigint NOT NULL
);


--
-- Name: transaction_audit; Type: TABLE; Schema: instantor; Owner: -
--

CREATE TABLE transaction_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    account_holder_name text,
    account_number text,
    amount numeric(19,2),
    balance numeric(19,2),
    category text,
    client_id bigint,
    currency text,
    date date,
    description text,
    response_id bigint
);


--
-- Name: response_pkey; Type: CONSTRAINT; Schema: instantor; Owner: -
--

ALTER TABLE ONLY response
    ADD CONSTRAINT response_pkey PRIMARY KEY (id);


--
-- Name: transaction_audit_pkey; Type: CONSTRAINT; Schema: instantor; Owner: -
--

ALTER TABLE ONLY transaction_audit
    ADD CONSTRAINT transaction_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: transaction_pkey; Type: CONSTRAINT; Schema: instantor; Owner: -
--

ALTER TABLE ONLY transaction
    ADD CONSTRAINT transaction_pkey PRIMARY KEY (id);


--
-- Name: idx_response_client_id; Type: INDEX; Schema: instantor; Owner: -
--

CREATE INDEX idx_response_client_id ON response USING btree (client_id);


--
-- Name: fkitl5jybf4md2jww10iu2f7dcc; Type: FK CONSTRAINT; Schema: instantor; Owner: -
--

ALTER TABLE ONLY transaction_audit
    ADD CONSTRAINT fkitl5jybf4md2jww10iu2f7dcc FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

ALTER TABLE instantor.transaction ADD CONSTRAINT fk_transaction_response_id FOREIGN KEY (response_id) REFERENCES instantor.response (id);
CREATE INDEX IF NOT EXISTS idx_transaction_response_id ON instantor.transaction USING btree (response_id);
