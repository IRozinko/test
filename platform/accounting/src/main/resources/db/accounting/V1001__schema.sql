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
-- Name: accounting; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS accounting;


SET search_path = accounting, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: account; Type: TABLE; Schema: accounting; Owner: -
--

CREATE TABLE account (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    code text NOT NULL,
    name text NOT NULL
);


--
-- Name: account_audit; Type: TABLE; Schema: accounting; Owner: -
--

CREATE TABLE account_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    code text,
    name text
);


--
-- Name: entry; Type: TABLE; Schema: accounting; Owner: -
--

CREATE TABLE entry (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    amount numeric(19,4) NOT NULL,
    booking_date date NOT NULL,
    client_id bigint,
    credit numeric(19,4) NOT NULL,
    debit numeric(19,4) NOT NULL,
    disbursement_id bigint,
    entry_type text NOT NULL,
    institution_account_id bigint,
    institution_id bigint,
    invoice_id bigint,
    loan_id bigint,
    payment_id bigint,
    post_date date NOT NULL,
    product_id bigint,
    transaction_id bigint NOT NULL,
    transaction_type text NOT NULL,
    value_date date NOT NULL,
    account_id bigint NOT NULL
);


--
-- Name: entry_audit; Type: TABLE; Schema: accounting; Owner: -
--

CREATE TABLE entry_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    amount numeric(19,4),
    booking_date date,
    client_id bigint,
    credit numeric(19,4),
    debit numeric(19,4),
    disbursement_id bigint,
    entry_type text,
    institution_account_id bigint,
    institution_id bigint,
    invoice_id bigint,
    loan_id bigint,
    payment_id bigint,
    post_date date,
    product_id bigint,
    transaction_id bigint,
    transaction_type text,
    value_date date,
    account_id bigint
);


--
-- Name: account_audit_pkey; Type: CONSTRAINT; Schema: accounting; Owner: -
--

ALTER TABLE ONLY account_audit
    ADD CONSTRAINT account_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: account_pkey; Type: CONSTRAINT; Schema: accounting; Owner: -
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- Name: entry_audit_pkey; Type: CONSTRAINT; Schema: accounting; Owner: -
--

ALTER TABLE ONLY entry_audit
    ADD CONSTRAINT entry_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: entry_pkey; Type: CONSTRAINT; Schema: accounting; Owner: -
--

ALTER TABLE ONLY entry
    ADD CONSTRAINT entry_pkey PRIMARY KEY (id);


--
-- Name: uk_k9qlqijt38kmryafdhhq04lon; Type: CONSTRAINT; Schema: accounting; Owner: -
--

ALTER TABLE ONLY account
    ADD CONSTRAINT uk_k9qlqijt38kmryafdhhq04lon UNIQUE (code);


--
-- Name: idx_account_code; Type: INDEX; Schema: accounting; Owner: -
--

CREATE INDEX idx_account_code ON account USING btree (code);


--
-- Name: idx_entry_account_id; Type: INDEX; Schema: accounting; Owner: -
--

CREATE INDEX idx_entry_account_id ON entry USING btree (account_id);


--
-- Name: idx_entry_client_id; Type: INDEX; Schema: accounting; Owner: -
--

CREATE INDEX idx_entry_client_id ON entry USING btree (client_id);


--
-- Name: idx_entry_loan_id; Type: INDEX; Schema: accounting; Owner: -
--

CREATE INDEX idx_entry_loan_id ON entry USING btree (loan_id);


--
-- Name: idx_entry_post_date; Type: INDEX; Schema: accounting; Owner: -
--

CREATE INDEX idx_entry_post_date ON entry USING btree (post_date);


--
-- Name: idx_entry_transaction_id; Type: INDEX; Schema: accounting; Owner: -
--

CREATE INDEX idx_entry_transaction_id ON entry USING btree (transaction_id);


--
-- Name: idx_entry_value_date; Type: INDEX; Schema: accounting; Owner: -
--

CREATE INDEX idx_entry_value_date ON entry USING btree (value_date);



--
-- Name: fk8lqvppo57etaqik9q1sr91bvr; Type: FK CONSTRAINT; Schema: accounting; Owner: -
--

ALTER TABLE ONLY entry_audit
    ADD CONSTRAINT fk8lqvppo57etaqik9q1sr91bvr FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkc5q1ytk3h4gxckmb6ghro1u58; Type: FK CONSTRAINT; Schema: accounting; Owner: -
--

ALTER TABLE ONLY account_audit
    ADD CONSTRAINT fkc5q1ytk3h4gxckmb6ghro1u58 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

ALTER TABLE accounting.entry ADD CONSTRAINT fk_entry_account_id FOREIGN KEY (account_id) REFERENCES accounting.account (id);
CREATE INDEX IF NOT EXISTS idx_entry_account_id ON accounting.entry USING btree (account_id);
