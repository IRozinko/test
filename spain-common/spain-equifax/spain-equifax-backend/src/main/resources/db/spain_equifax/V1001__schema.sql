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
-- Name: spain_equifax; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS spain_equifax;


SET search_path = spain_equifax, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: equifax; Type: TABLE; Schema: spain_equifax; Owner: -
--

CREATE TABLE equifax (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    client_id bigint,
    delincuency_days bigint NOT NULL,
    document_number text,
    error text,
    number_of_consumer_credit_operations bigint NOT NULL,
    number_of_credit_card_operations bigint NOT NULL,
    number_of_creditors bigint NOT NULL,
    number_of_days_of_worst_situation bigint NOT NULL,
    number_of_mortgage_operations bigint NOT NULL,
    number_of_personal_loan_operations bigint NOT NULL,
    number_of_telco_operations bigint NOT NULL,
    request_body text,
    response_body text,
    status text NOT NULL,
    total_number_of_operations bigint NOT NULL,
    total_number_of_other_unpaid bigint NOT NULL,
    total_unpaid_balance numeric(19,2) NOT NULL,
    unpaid_balance_of_consumer_credit numeric(19,2) NOT NULL,
    unpaid_balance_of_credit_card numeric(19,2) NOT NULL,
    unpaid_balance_of_mortgage numeric(19,2) NOT NULL,
    unpaid_balance_of_other numeric(19,2) NOT NULL,
    unpaid_balance_of_other_products numeric(19,2) NOT NULL,
    unpaid_balance_of_personal_loan numeric(19,2) NOT NULL,
    unpaid_balance_of_telco numeric(19,2) NOT NULL,
    unpaid_balance_own_entity numeric(19,2) NOT NULL,
    worst_situation_code text,
    worst_unpaid_balance numeric(19,2) NOT NULL
);


--
-- Name: equifax_pkey; Type: CONSTRAINT; Schema: spain_equifax; Owner: -
--

ALTER TABLE ONLY equifax
    ADD CONSTRAINT equifax_pkey PRIMARY KEY (id);


--
-- Name: idx_equifax_client_id; Type: INDEX; Schema: spain_equifax; Owner: -
--

CREATE INDEX idx_equifax_client_id ON equifax USING btree (client_id);


--
-- PostgreSQL database dump complete
--

