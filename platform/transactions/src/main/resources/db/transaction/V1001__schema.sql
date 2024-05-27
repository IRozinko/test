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
-- Name: transaction; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS transaction;


SET search_path = transaction, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: transaction; Type: TABLE; Schema: transaction; Owner: -
--

CREATE TABLE transaction (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    booking_date date NOT NULL,
    cash_in numeric(19,4) NOT NULL,
    cash_out numeric(19,4) NOT NULL,
    client_id bigint,
    comments text,
    disbursement_id bigint,
    fee_applied numeric(19,4) NOT NULL,
    fee_invoiced numeric(19,4) NOT NULL,
    fee_paid numeric(19,4) NOT NULL,
    fee_written_off numeric(19,4) NOT NULL,
    institution_account_id bigint,
    institution_id bigint,
    interest_applied numeric(19,4) NOT NULL,
    interest_invoiced numeric(19,4) NOT NULL,
    interest_paid numeric(19,4) NOT NULL,
    interest_written_off numeric(19,4) NOT NULL,
    invoice_id bigint,
    loan_id bigint,
    overpayment_received numeric(19,4) NOT NULL,
    overpayment_refunded numeric(19,4) NOT NULL,
    overpayment_used numeric(19,4) NOT NULL,
    payment_id bigint,
    penalty_applied numeric(19,4) NOT NULL,
    penalty_invoiced numeric(19,4) NOT NULL,
    penalty_paid numeric(19,4) NOT NULL,
    penalty_written_off numeric(19,4) NOT NULL,
    post_date date NOT NULL,
    principal_disbursed numeric(19,4) NOT NULL,
    principal_invoiced numeric(19,4) NOT NULL,
    principal_paid numeric(19,4) NOT NULL,
    principal_written_off numeric(19,4) NOT NULL,
    product_id bigint,
    transaction_sub_type text,
    transaction_type text NOT NULL,
    value_date date NOT NULL,
    voided boolean NOT NULL,
    voided_date date,
    voids_transaction_id bigint
);


--
-- Name: transaction_audit; Type: TABLE; Schema: transaction; Owner: -
--

CREATE TABLE transaction_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    booking_date date,
    cash_in numeric(19,4),
    cash_out numeric(19,4),
    client_id bigint,
    comments text,
    disbursement_id bigint,
    fee_applied numeric(19,4),
    fee_invoiced numeric(19,4),
    fee_paid numeric(19,4),
    fee_written_off numeric(19,4),
    institution_account_id bigint,
    institution_id bigint,
    interest_applied numeric(19,4),
    interest_invoiced numeric(19,4),
    interest_paid numeric(19,4),
    interest_written_off numeric(19,4),
    invoice_id bigint,
    loan_id bigint,
    overpayment_received numeric(19,4),
    overpayment_refunded numeric(19,4),
    overpayment_used numeric(19,4),
    payment_id bigint,
    penalty_applied numeric(19,4),
    penalty_invoiced numeric(19,4),
    penalty_paid numeric(19,4),
    penalty_written_off numeric(19,4),
    post_date date,
    principal_disbursed numeric(19,4),
    principal_invoiced numeric(19,4),
    principal_paid numeric(19,4),
    principal_written_off numeric(19,4),
    product_id bigint,
    transaction_sub_type text,
    transaction_type text,
    value_date date,
    voided boolean,
    voided_date date,
    voids_transaction_id bigint
);


--
-- Name: transaction_entry; Type: TABLE; Schema: transaction; Owner: -
--

CREATE TABLE transaction_entry (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    amount_applied numeric(19,4) NOT NULL,
    amount_invoiced numeric(19,4) NOT NULL,
    amount_paid numeric(19,4) NOT NULL,
    amount_written_off numeric(19,4) NOT NULL,
    fee_id bigint,
    sub_type text,
    type text,
    transaction_id bigint NOT NULL
);


--
-- Name: transaction_entry_audit; Type: TABLE; Schema: transaction; Owner: -
--

CREATE TABLE transaction_entry_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    amount_applied numeric(19,4),
    amount_invoiced numeric(19,4),
    amount_paid numeric(19,4),
    amount_written_off numeric(19,4),
    fee_id bigint,
    sub_type text,
    type text,
    transaction_id bigint
);


--
-- Name: transaction_audit_pkey; Type: CONSTRAINT; Schema: transaction; Owner: -
--

ALTER TABLE ONLY transaction_audit
    ADD CONSTRAINT transaction_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: transaction_entry_audit_pkey; Type: CONSTRAINT; Schema: transaction; Owner: -
--

ALTER TABLE ONLY transaction_entry_audit
    ADD CONSTRAINT transaction_entry_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: transaction_entry_pkey; Type: CONSTRAINT; Schema: transaction; Owner: -
--

ALTER TABLE ONLY transaction_entry
    ADD CONSTRAINT transaction_entry_pkey PRIMARY KEY (id);


--
-- Name: transaction_pkey; Type: CONSTRAINT; Schema: transaction; Owner: -
--

ALTER TABLE ONLY transaction
    ADD CONSTRAINT transaction_pkey PRIMARY KEY (id);


--
-- Name: idx_transaction_booking_date; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_booking_date ON transaction USING btree (booking_date);


--
-- Name: idx_transaction_client_id; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_client_id ON transaction USING btree (client_id);


--
-- Name: idx_transaction_disbursement_id; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_disbursement_id ON transaction USING btree (disbursement_id);


--
-- Name: idx_transaction_entry_fee_id; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_entry_fee_id ON transaction_entry USING btree (fee_id);


--
-- Name: idx_transaction_entry_transaction_id; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_entry_transaction_id ON transaction_entry USING btree (transaction_id);


--
-- Name: idx_transaction_invoice_id; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_invoice_id ON transaction USING btree (invoice_id);


--
-- Name: idx_transaction_loan_id; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_loan_id ON transaction USING btree (loan_id);


--
-- Name: idx_transaction_payment_id; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_payment_id ON transaction USING btree (payment_id);


--
-- Name: idx_transaction_value_date; Type: INDEX; Schema: transaction; Owner: -
--

CREATE INDEX idx_transaction_value_date ON transaction USING btree (value_date);


--
-- Name: fkbxyr4tsv24uvs1o8f21abwa2s; Type: FK CONSTRAINT; Schema: transaction; Owner: -
--

ALTER TABLE ONLY transaction_entry_audit
    ADD CONSTRAINT fkbxyr4tsv24uvs1o8f21abwa2s FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkitl5jybf4md2jww10iu2f7dcc; Type: FK CONSTRAINT; Schema: transaction; Owner: -
--

ALTER TABLE ONLY transaction_audit
    ADD CONSTRAINT fkitl5jybf4md2jww10iu2f7dcc FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--



ALTER TABLE transaction.transaction_entry ADD CONSTRAINT fk_transaction_entry_transaction_id FOREIGN KEY (transaction_id) REFERENCES transaction.transaction (id);
CREATE INDEX IF NOT EXISTS idx_transaction_entry_transaction_id ON transaction.transaction_entry USING btree (transaction_id);

ALTER TABLE transaction.transaction ADD CONSTRAINT fk_transaction_voids_transaction_id FOREIGN KEY (voids_transaction_id) REFERENCES transaction.transaction(id);
CREATE INDEX IF NOT EXISTS idx_transaction_voids_transaction_id ON transaction.transaction USING btree (voids_transaction_id);
