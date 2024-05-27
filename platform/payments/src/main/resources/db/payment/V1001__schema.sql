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
-- Name: payment; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS payment;


SET search_path = payment, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: disbursement; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE disbursement (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    amount numeric(19,2) NOT NULL,
    client_id bigint,
    error text,
    exported_at timestamp without time zone,
    exported_cloud_file_id bigint,
    exported_file_name text,
    institution_account_id bigint,
    institution_id bigint NOT NULL,
    loan_id bigint,
    reference text NOT NULL,
    settled_at timestamp without time zone,
    status text NOT NULL,
    status_detail text NOT NULL,
    value_date date NOT NULL
);


--
-- Name: disbursement_audit; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE disbursement_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    amount numeric(19,2),
    client_id bigint,
    error text,
    exported_at timestamp without time zone,
    exported_cloud_file_id bigint,
    exported_file_name text,
    institution_account_id bigint,
    institution_id bigint,
    loan_id bigint,
    reference text,
    settled_at timestamp without time zone,
    status text,
    status_detail text,
    value_date date
);


--
-- Name: institution; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE institution (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    institution_type text NOT NULL,
    name text NOT NULL,
    payment_methods text,
    is_primary boolean NOT NULL,
    statement_export_format text,
    statement_export_params_json text,
    statement_import_format text
);


--
-- Name: institution_account; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE institution_account (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    account_number text NOT NULL,
    accounting_account_code text NOT NULL,
    is_primary boolean NOT NULL,
    institution_id bigint NOT NULL
);


--
-- Name: institution_account_audit; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE institution_account_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    account_number text,
    accounting_account_code text,
    is_primary boolean,
    institution_id bigint
);


--
-- Name: institution_audit; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE institution_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    institution_type text,
    name text,
    payment_methods text,
    is_primary boolean,
    statement_export_format text,
    statement_export_params_json text,
    statement_import_format text
);


--
-- Name: payment; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE payment (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    amount numeric(19,2) NOT NULL,
    counterparty_account text,
    counterparty_address text,
    counterparty_name text,
    details text,
    key text NOT NULL,
    payment_type text NOT NULL,
    pending_amount numeric(19,2) NOT NULL,
    posted_at timestamp without time zone,
    reference text,
    status text NOT NULL,
    status_detail text NOT NULL,
    value_date date NOT NULL,
    account_id bigint NOT NULL
);


--
-- Name: payment_audit; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE payment_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    amount numeric(19,2),
    counterparty_account text,
    counterparty_address text,
    counterparty_name text,
    details text,
    key text,
    payment_type text,
    pending_amount numeric(19,2),
    posted_at timestamp without time zone,
    reference text,
    status text,
    status_detail text,
    value_date date,
    account_id bigint
);


--
-- Name: statement; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE statement (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    account_number text,
    end_date date,
    error text,
    file_id bigint NOT NULL,
    file_name text,
    format text,
    institution_id bigint,
    start_date date,
    status text NOT NULL
);


--
-- Name: statement_row; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE statement_row (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    account_number text NOT NULL,
    amount numeric(19,2) NOT NULL,
    balance numeric(19,2) NOT NULL,
    counterparty_account text,
    counterparty_address text,
    counterparty_name text,
    currency text NOT NULL,
    date date NOT NULL,
    description text NOT NULL,
    key text,
    payment_id bigint,
    reference text,
    source_json text,
    status text NOT NULL,
    status_message text,
    suggested_transaction_sub_type text,
    transaction_code text,
    value_date date NOT NULL,
    statement_id bigint NOT NULL
);


--
-- Name: statement_row_attributes; Type: TABLE; Schema: payment; Owner: -
--

CREATE TABLE statement_row_attributes (
    statement_row_id bigint NOT NULL,
    value text,
    key text NOT NULL
);


--
-- Name: disbursement_audit_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY disbursement_audit
    ADD CONSTRAINT disbursement_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: disbursement_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY disbursement
    ADD CONSTRAINT disbursement_pkey PRIMARY KEY (id);


--
-- Name: idx_payment_key; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT idx_payment_key UNIQUE (key);


--
-- Name: institution_account_audit_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY institution_account_audit
    ADD CONSTRAINT institution_account_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: institution_account_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY institution_account
    ADD CONSTRAINT institution_account_pkey PRIMARY KEY (id);


--
-- Name: institution_audit_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY institution_audit
    ADD CONSTRAINT institution_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: institution_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY institution
    ADD CONSTRAINT institution_pkey PRIMARY KEY (id);


--
-- Name: payment_audit_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY payment_audit
    ADD CONSTRAINT payment_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: payment_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (id);


--
-- Name: statement_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY statement
    ADD CONSTRAINT statement_pkey PRIMARY KEY (id);


--
-- Name: statement_row_attributes_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY statement_row_attributes
    ADD CONSTRAINT statement_row_attributes_pkey PRIMARY KEY (statement_row_id, key);


--
-- Name: statement_row_pkey; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY statement_row
    ADD CONSTRAINT statement_row_pkey PRIMARY KEY (id);


--
-- Name: uk_qhw15h5f7nc4g3ndva8sory1u; Type: CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY institution
    ADD CONSTRAINT uk_qhw15h5f7nc4g3ndva8sory1u UNIQUE (name);


--
-- Name: idx_disbursement_client_id; Type: INDEX; Schema: payment; Owner: -
--

CREATE INDEX idx_disbursement_client_id ON disbursement USING btree (client_id);


--
-- Name: idx_disbursement_loan_id; Type: INDEX; Schema: payment; Owner: -
--

CREATE INDEX idx_disbursement_loan_id ON disbursement USING btree (loan_id);


--
-- Name: idx_statement_row_key; Type: INDEX; Schema: payment; Owner: -
--

CREATE INDEX idx_statement_row_key ON statement_row USING btree (key);



--
-- Name: fk346fakdusvutj05ht1y98ascs; Type: FK CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY institution_account_audit
    ADD CONSTRAINT fk346fakdusvutj05ht1y98ascs FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkbgrxj0wf91bibhcfggol42gqp; Type: FK CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY institution_audit
    ADD CONSTRAINT fkbgrxj0wf91bibhcfggol42gqp FOREIGN KEY (rev) REFERENCES common.revision(id);



--
-- Name: fkimytpo198v7bhg7ro9tuwn1d5; Type: FK CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY disbursement_audit
    ADD CONSTRAINT fkimytpo198v7bhg7ro9tuwn1d5 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkk7upb20uhwv1egfhgqq0y9tu2; Type: FK CONSTRAINT; Schema: payment; Owner: -
--

ALTER TABLE ONLY payment_audit
    ADD CONSTRAINT fkk7upb20uhwv1egfhgqq0y9tu2 FOREIGN KEY (rev) REFERENCES common.revision(id);



--
-- PostgreSQL database dump complete
--


ALTER TABLE payment.statement_row_attributes ADD CONSTRAINT fk_statement_row_attributes_statement_row_id FOREIGN KEY (statement_row_id) REFERENCES payment.statement_row (id);
CREATE INDEX IF NOT EXISTS idx_statement_row_attributes_statement_row_id ON payment.statement_row_attributes USING btree (statement_row_id);

ALTER TABLE payment.statement_row ADD CONSTRAINT fk_statement_row_statement_id FOREIGN KEY (statement_id) REFERENCES payment.statement (id);
CREATE INDEX IF NOT EXISTS idx_statement_row_statement_id ON payment.statement_row USING btree (statement_id);

ALTER TABLE payment.statement_row ADD CONSTRAINT fk_statement_row_payment_id FOREIGN KEY (payment_id) REFERENCES payment.payment (id);
CREATE INDEX IF NOT EXISTS idx_statement_row_payment_id ON payment.statement_row USING btree (payment_id);

ALTER TABLE payment.statement ADD CONSTRAINT fk_statement_institution_id FOREIGN KEY (institution_id) REFERENCES payment.institution (id);
CREATE INDEX IF NOT EXISTS idx_statement_institution_id ON payment.statement USING btree (institution_id);

ALTER TABLE payment.payment ADD CONSTRAINT fk_payment_account_id FOREIGN KEY (account_id) REFERENCES payment.institution_account (id);
CREATE INDEX IF NOT EXISTS idx_payment_account_id ON payment.payment USING btree (account_id);

ALTER TABLE payment.institution_account ADD CONSTRAINT fk_institution_account_institution_id FOREIGN KEY (institution_id) REFERENCES payment.institution (id);
 CREATE INDEX IF NOT EXISTS idx_institution_account_institution_id ON payment.institution_account USING btree (institution_id);

ALTER TABLE payment.disbursement ADD CONSTRAINT fk_disbursement_institution_id FOREIGN KEY (institution_id) REFERENCES payment.institution (id);
CREATE INDEX IF NOT EXISTS idx_disbursement_institution_id ON payment.disbursement USING btree (institution_id);
