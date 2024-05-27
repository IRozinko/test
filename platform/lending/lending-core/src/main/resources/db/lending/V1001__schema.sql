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
-- Name: lending; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS lending;


SET search_path = lending, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: credit_limit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE credit_limit (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    active_from date NOT NULL,
    client_id bigint NOT NULL,
    credit_limit numeric(19,2) NOT NULL,
    reason text NOT NULL
);


--
-- Name: credit_limit_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE credit_limit_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    active_from date,
    client_id bigint,
    credit_limit numeric(19,2),
    reason text
);


--
-- Name: fee; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE fee (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    amount_applied numeric(19,4) NOT NULL,
    amount_invoiced numeric(19,4) NOT NULL,
    amount_outstanding numeric(19,4) NOT NULL,
    amount_paid numeric(19,4) NOT NULL,
    amount_written_off numeric(19,4) NOT NULL,
    applied_date date NOT NULL,
    auto_void_on_unpaid boolean NOT NULL,
    client_id bigint NOT NULL,
    custom_data_json text,
    fee_type text NOT NULL,
    loan_application_id bigint,
    loan_id bigint,
    paid_date date,
    product_id bigint,
    status text NOT NULL,
    status_detail text NOT NULL,
    voided_date date
);


--
-- Name: fee_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE fee_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    amount_applied numeric(19,4),
    amount_invoiced numeric(19,4),
    amount_outstanding numeric(19,4),
    amount_paid numeric(19,4),
    amount_written_off numeric(19,4),
    applied_date date,
    auto_void_on_unpaid boolean,
    client_id bigint,
    custom_data_json text,
    fee_type text,
    loan_application_id bigint,
    loan_id bigint,
    paid_date date,
    product_id bigint,
    status text,
    status_detail text,
    voided_date date
);


--
-- Name: invoice; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE invoice (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    client_id bigint NOT NULL,
    close_date timestamp without time zone,
    close_reason text,
    corrections integer NOT NULL,
    due_date date NOT NULL,
    file_id bigint,
    file_name text,
    invoice_date date NOT NULL,
    loan_id bigint NOT NULL,
    number text NOT NULL,
    period_from date NOT NULL,
    period_to date NOT NULL,
    product_id bigint NOT NULL,
    status text NOT NULL,
    status_detail text,
    total numeric(19,2) NOT NULL,
    total_paid numeric(19,2) NOT NULL,
    voided boolean NOT NULL
);


--
-- Name: invoice_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE invoice_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    client_id bigint,
    close_date timestamp without time zone,
    close_reason text,
    corrections integer,
    due_date date,
    file_id bigint,
    file_name text,
    invoice_date date,
    loan_id bigint,
    number text,
    period_from date,
    period_to date,
    product_id bigint,
    status text,
    status_detail text,
    total numeric(19,2),
    total_paid numeric(19,2),
    voided boolean
);


--
-- Name: invoice_item; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE invoice_item (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    amount numeric(19,2) NOT NULL,
    amount_paid numeric(19,2) NOT NULL,
    correction boolean NOT NULL,
    fee_id bigint,
    loan_id bigint NOT NULL,
    sub_type text,
    type text NOT NULL,
    invoice_id bigint NOT NULL
);


--
-- Name: invoice_item_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE invoice_item_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    amount numeric(19,2),
    amount_paid numeric(19,2),
    correction boolean,
    fee_id bigint,
    loan_id bigint,
    sub_type text,
    type text,
    invoice_id bigint
);


--
-- Name: loan; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE loan (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    cash_in numeric(19,4) NOT NULL,
    cash_out numeric(19,4) NOT NULL,
    client_id bigint NOT NULL,
    close_date date,
    credit_limit numeric(19,2) NOT NULL,
    fee_applied numeric(19,4) NOT NULL,
    fee_due numeric(19,4) NOT NULL,
    fee_outstanding numeric(19,4) NOT NULL,
    fee_paid numeric(19,4) NOT NULL,
    fee_written_off numeric(19,4) NOT NULL,
    interest_applied numeric(19,4) NOT NULL,
    interest_due numeric(19,4) NOT NULL,
    interest_outstanding numeric(19,4) NOT NULL,
    interest_paid numeric(19,4) NOT NULL,
    interest_written_off numeric(19,4) NOT NULL,
    invoice_payment_day integer NOT NULL,
    issue_date date NOT NULL,
    loan_application_id bigint NOT NULL,
    loans_paid bigint NOT NULL,
    maturity_date date,
    max_overdue_days integer NOT NULL,
    loan_number text NOT NULL,
    overdue_days integer NOT NULL,
    payment_due_date date,
    penalty_applied numeric(19,4) NOT NULL,
    penalty_due numeric(19,4) NOT NULL,
    penalty_outstanding numeric(19,4) NOT NULL,
    penalty_paid numeric(19,4) NOT NULL,
    penalty_written_off numeric(19,4) NOT NULL,
    principal_disbursed numeric(19,4) NOT NULL,
    principal_due numeric(19,4) NOT NULL,
    principal_outstanding numeric(19,4) NOT NULL,
    principal_paid numeric(19,4) NOT NULL,
    principal_written_off numeric(19,4) NOT NULL,
    product_id bigint NOT NULL,
    status text NOT NULL,
    status_detail text NOT NULL,
    total_due numeric(19,4) NOT NULL,
    total_outstanding numeric(19,4) NOT NULL
);


--
-- Name: loan_application; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE loan_application (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    client_id bigint NOT NULL,
    close_date date,
    close_reason text,
    credit_limit numeric(19,2) NOT NULL,
    invoice_payment_day integer NOT NULL,
    ip_address text,
    ip_country text,
    loan_id bigint,
    loans_paid bigint,
    long_approve_code text,
    application_number text NOT NULL,
    offer_date date,
    offered_installments bigint NOT NULL,
    offered_interest numeric(19,2) NOT NULL,
    offered_period_count bigint NOT NULL,
    offered_period_unit text NOT NULL,
    offered_principal numeric(19,2) NOT NULL,
    product_id bigint NOT NULL,
    requested_installments bigint NOT NULL,
    requested_period_count bigint NOT NULL,
    requested_period_unit text NOT NULL,
    requested_principal numeric(19,2) NOT NULL,
    score numeric(19,2) NOT NULL,
    score_bucket text,
    short_approve_code text,
    status text NOT NULL,
    status_detail text NOT NULL,
    submitted_at timestamp without time zone NOT NULL,
    type text,
    workflow_id bigint,
    nominal_apr numeric(19,2) NOT NULL,
    effective_apr numeric(19,2) NOT NULL
);


--
-- Name: loan_application_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE loan_application_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    client_id bigint,
    close_date date,
    close_reason text,
    credit_limit numeric(19,2),
    invoice_payment_day integer,
    ip_address text,
    ip_country text,
    loan_id bigint,
    loans_paid bigint,
    long_approve_code text,
    application_number text,
    offer_date date,
    offered_installments bigint,
    offered_interest numeric(19,2),
    offered_period_count bigint,
    offered_period_unit text,
    offered_principal numeric(19,2),
    product_id bigint,
    requested_installments bigint,
    requested_period_count bigint,
    requested_period_unit text,
    requested_principal numeric(19,2),
    score numeric(19,2),
    score_bucket text,
    short_approve_code text,
    status text,
    status_detail text,
    submitted_at timestamp without time zone,
    type text,
    workflow_id bigint,
    nominal_apr numeric(19,2),
    effective_apr numeric(19,2) NOT NULL
);


--
-- Name: loan_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE loan_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    cash_in numeric(19,4),
    cash_out numeric(19,4),
    client_id bigint,
    close_date date,
    credit_limit numeric(19,2),
    fee_applied numeric(19,4),
    fee_due numeric(19,4),
    fee_outstanding numeric(19,4),
    fee_paid numeric(19,4),
    fee_written_off numeric(19,4),
    interest_applied numeric(19,4),
    interest_due numeric(19,4),
    interest_outstanding numeric(19,4),
    interest_paid numeric(19,4),
    interest_written_off numeric(19,4),
    invoice_payment_day integer,
    issue_date date,
    loan_application_id bigint,
    loans_paid bigint,
    maturity_date date,
    max_overdue_days integer,
    loan_number text,
    overdue_days integer,
    payment_due_date date,
    penalty_applied numeric(19,4),
    penalty_due numeric(19,4),
    penalty_outstanding numeric(19,4),
    penalty_paid numeric(19,4),
    penalty_written_off numeric(19,4),
    principal_disbursed numeric(19,4),
    principal_due numeric(19,4),
    principal_outstanding numeric(19,4),
    principal_paid numeric(19,4),
    principal_written_off numeric(19,4),
    product_id bigint,
    status text,
    status_detail text,
    total_due numeric(19,4),
    total_outstanding numeric(19,4)
);


--
-- Name: loan_daily_snapshot; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE loan_daily_snapshot (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    cash_in numeric(19,4) NOT NULL,
    cash_out numeric(19,4) NOT NULL,
    client_id bigint NOT NULL,
    close_date date,
    credit_limit numeric(19,2) NOT NULL,
    effective_from date NOT NULL,
    effective_to date NOT NULL,
    fee_applied numeric(19,4) NOT NULL,
    fee_due numeric(19,4) NOT NULL,
    fee_outstanding numeric(19,4) NOT NULL,
    fee_paid numeric(19,4) NOT NULL,
    fee_written_off numeric(19,4) NOT NULL,
    interest_applied numeric(19,4) NOT NULL,
    interest_due numeric(19,4) NOT NULL,
    interest_outstanding numeric(19,4) NOT NULL,
    interest_paid numeric(19,4) NOT NULL,
    interest_written_off numeric(19,4) NOT NULL,
    invoice_payment_day integer NOT NULL,
    issue_date date NOT NULL,
    latest boolean NOT NULL,
    loan_application_id bigint NOT NULL,
    loan_id bigint NOT NULL,
    loans_paid bigint NOT NULL,
    maturity_date date,
    max_overdue_days integer NOT NULL,
    loan_number text NOT NULL,
    overdue_days integer NOT NULL,
    payment_due_date date,
    penalty_applied numeric(19,4) NOT NULL,
    penalty_due numeric(19,4) NOT NULL,
    penalty_outstanding numeric(19,4) NOT NULL,
    penalty_paid numeric(19,4) NOT NULL,
    penalty_written_off numeric(19,4) NOT NULL,
    principal_disbursed numeric(19,4) NOT NULL,
    principal_due numeric(19,4) NOT NULL,
    principal_outstanding numeric(19,4) NOT NULL,
    principal_paid numeric(19,4) NOT NULL,
    principal_written_off numeric(19,4) NOT NULL,
    product_id bigint NOT NULL,
    status text NOT NULL,
    status_detail text NOT NULL,
    total_due numeric(19,4) NOT NULL,
    total_outstanding numeric(19,4) NOT NULL
);


--
-- Name: period; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE period (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    close_date date,
    closing_ended_at timestamp without time zone,
    closing_started_at timestamp without time zone,
    period_date date NOT NULL,
    result_log text,
    status text,
    status_detail text
);


--
-- Name: period_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE period_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    close_date date,
    closing_ended_at timestamp without time zone,
    closing_started_at timestamp without time zone,
    period_date date,
    result_log text,
    status text,
    status_detail text
);


--
-- Name: product; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE product (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    default_settings_json text NOT NULL,
    entity_version bigint NOT NULL,
    product_type text NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text
);


--
-- Name: product_audit; Type: TABLE; Schema: lending; Owner: -
--

CREATE TABLE product_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    default_settings_json text,
    product_type text,
    updated_at timestamp without time zone,
    updated_by text
);


--
-- Name: credit_limit_audit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY credit_limit_audit
    ADD CONSTRAINT credit_limit_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: credit_limit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY credit_limit
    ADD CONSTRAINT credit_limit_pkey PRIMARY KEY (id);


--
-- Name: fee_audit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY fee_audit
    ADD CONSTRAINT fee_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: fee_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY fee
    ADD CONSTRAINT fee_pkey PRIMARY KEY (id);


--
-- Name: invoice_audit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY invoice_audit
    ADD CONSTRAINT invoice_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: invoice_item_audit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY invoice_item_audit
    ADD CONSTRAINT invoice_item_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: invoice_item_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY invoice_item
    ADD CONSTRAINT invoice_item_pkey PRIMARY KEY (id);


--
-- Name: invoice_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY invoice
    ADD CONSTRAINT invoice_pkey PRIMARY KEY (id);


--
-- Name: loan_application_audit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan_application_audit
    ADD CONSTRAINT loan_application_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: loan_application_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan_application
    ADD CONSTRAINT loan_application_pkey PRIMARY KEY (id);


--
-- Name: loan_audit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan_audit
    ADD CONSTRAINT loan_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: loan_daily_snapshot_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan_daily_snapshot
    ADD CONSTRAINT loan_daily_snapshot_pkey PRIMARY KEY (id);


--
-- Name: loan_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan
    ADD CONSTRAINT loan_pkey PRIMARY KEY (id);


--
-- Name: period_audit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY period_audit
    ADD CONSTRAINT period_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: period_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY period
    ADD CONSTRAINT period_pkey PRIMARY KEY (id);


--
-- Name: product_audit_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY product_audit
    ADD CONSTRAINT product_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: product_pkey; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);


--
-- Name: uk_2eb5cl6cjlr0wxq8a5098rqxb; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY period
    ADD CONSTRAINT uk_2eb5cl6cjlr0wxq8a5098rqxb UNIQUE (period_date);


--
-- Name: uk_5wwvsnbyhmf11rotfyqxyrv6c; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan_application
    ADD CONSTRAINT uk_5wwvsnbyhmf11rotfyqxyrv6c UNIQUE (application_number);


--
-- Name: uk_fxya0bow1ym2yjpnh2r2lijv1; Type: CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan
    ADD CONSTRAINT uk_fxya0bow1ym2yjpnh2r2lijv1 UNIQUE (loan_number);


--
-- Name: idx_credit_limit_client_id; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_credit_limit_client_id ON credit_limit USING btree (client_id);


--
-- Name: idx_fee_client_id; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_fee_client_id ON fee USING btree (client_id);


--
-- Name: idx_fee_loan_id; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_fee_loan_id ON fee USING btree (loan_id);


--
-- Name: idx_invoice_client_id; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_invoice_client_id ON invoice USING btree (client_id);


--
-- Name: idx_invoice_due_date; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_invoice_due_date ON invoice USING btree (due_date);


--
-- Name: idx_invoice_invoice_date; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_invoice_invoice_date ON invoice USING btree (invoice_date);


--
-- Name: idx_invoice_loan_id; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_invoice_loan_id ON invoice USING btree (loan_id);


--
-- Name: idx_invoice_period_from; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_invoice_period_from ON invoice USING btree (period_from);


--
-- Name: idx_invoice_period_to; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_invoice_period_to ON invoice USING btree (period_to);


--
-- Name: idx_loan_application_client_id; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_loan_application_client_id ON loan_application USING btree (client_id);


--
-- Name: idx_loan_client_id; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_loan_client_id ON loan USING btree (client_id);


--
-- Name: idx_period_period_date; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_period_period_date ON period USING btree (period_date);


--
-- Name: idx_period_status; Type: INDEX; Schema: lending; Owner: -
--

CREATE INDEX idx_period_status ON period USING btree (status);


--
-- Name: fk4ntmns7tt2fl2qljs745eiyfg; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY fee_audit
    ADD CONSTRAINT fk4ntmns7tt2fl2qljs745eiyfg FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fk9vt2x91vjib9iajufwx896e9k; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY invoice_audit
    ADD CONSTRAINT fk9vt2x91vjib9iajufwx896e9k FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkaxegljb5dbo4hrv0oxvqbdkg1; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY product_audit
    ADD CONSTRAINT fkaxegljb5dbo4hrv0oxvqbdkg1 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkbu6tmpd0mtgu9wrw5bj5uv09v; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY invoice_item
    ADD CONSTRAINT fkbu6tmpd0mtgu9wrw5bj5uv09v FOREIGN KEY (invoice_id) REFERENCES invoice(id);


--
-- Name: fkcm13v29rshy42kj035vrcg83p; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan_audit
    ADD CONSTRAINT fkcm13v29rshy42kj035vrcg83p FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkmabper8j5makj2a0bddv8nfdf; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY loan_application_audit
    ADD CONSTRAINT fkmabper8j5makj2a0bddv8nfdf FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkn2t4c15n9qdb365if06077ys2; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY period_audit
    ADD CONSTRAINT fkn2t4c15n9qdb365if06077ys2 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fknd0un2w2gshqp15rybyr85imm; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY credit_limit_audit
    ADD CONSTRAINT fknd0un2w2gshqp15rybyr85imm FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkp0r3ug5scsnggtx1rq93jvlnd; Type: FK CONSTRAINT; Schema: lending; Owner: -
--

ALTER TABLE ONLY invoice_item_audit
    ADD CONSTRAINT fkp0r3ug5scsnggtx1rq93jvlnd FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

ALTER TABLE lending.loan ADD CONSTRAINT fk_loan_product_id FOREIGN KEY (product_id) REFERENCES lending.product (id);
CREATE INDEX IF NOT EXISTS idx_loan_product_id ON lending.loan USING btree (product_id);

ALTER TABLE lending.invoice_item ADD CONSTRAINT fk_invoice_item_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id);
CREATE INDEX IF NOT EXISTS idx_invoice_item_loan_id ON lending.invoice_item USING btree (loan_id);

ALTER TABLE lending.loan ADD CONSTRAINT fk_loan_loan_application_id FOREIGN KEY (loan_application_id) REFERENCES lending.loan_application (id);
CREATE INDEX IF NOT EXISTS idx_loan_loan_application_id ON lending.loan USING btree (loan_application_id);

ALTER TABLE lending.invoice_item ADD CONSTRAINT fk_invoice_item_invoice_id FOREIGN KEY (invoice_id) REFERENCES lending.invoice (id);
CREATE INDEX IF NOT EXISTS idx_invoice_item_invoice_id ON lending.invoice_item USING btree (invoice_id);

ALTER TABLE lending.invoice_item ADD CONSTRAINT fk_invoice_item_fee_id FOREIGN KEY (fee_id) REFERENCES lending.fee (id);
CREATE INDEX IF NOT EXISTS idx_invoice_item_fee_id ON lending.invoice_item USING btree (fee_id);

ALTER TABLE lending.invoice ADD CONSTRAINT fk_invoice_product_id FOREIGN KEY (product_id) REFERENCES lending.product (id);
CREATE INDEX IF NOT EXISTS idx_invoice_product_id ON lending.invoice USING btree (product_id);

ALTER TABLE lending.invoice ADD CONSTRAINT fk_invoice_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id);
CREATE INDEX IF NOT EXISTS idx_invoice_loan_id ON lending.invoice USING btree (loan_id);

ALTER TABLE lending.fee ADD CONSTRAINT fk_fee_product_id FOREIGN KEY (product_id) REFERENCES lending.product (id);
CREATE INDEX IF NOT EXISTS idx_fee_product_id ON lending.fee USING btree (product_id);

ALTER TABLE lending.fee ADD CONSTRAINT fk_fee_loan_id FOREIGN KEY (loan_id) REFERENCES lending.loan (id);
CREATE INDEX IF NOT EXISTS idx_fee_loan_id ON lending.fee USING btree (loan_id);

ALTER TABLE lending.fee ADD CONSTRAINT fk_fee_loan_application_id FOREIGN KEY (loan_application_id) REFERENCES lending.loan_application (id);
CREATE INDEX IF NOT EXISTS idx_fee_loan_application_id ON lending.fee USING btree (loan_application_id);
