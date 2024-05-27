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
-- Name: iovation; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS iovation;


SET search_path = iovation, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: blackbox; Type: TABLE; Schema: iovation; Owner: -
--

CREATE TABLE blackbox (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    black_box text NOT NULL,
    client_id bigint NOT NULL,
    ip_address text NOT NULL
);


--
-- Name: transaction; Type: TABLE; Schema: iovation; Owner: -
--

CREATE TABLE transaction (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    black_box text,
    client_id bigint,
    end_black_box text,
    error text,
    ip_address text NOT NULL,
    reason text,
    result text,
    status text NOT NULL,
    tracking_number text
);


--
-- Name: transaction_detail; Type: TABLE; Schema: iovation; Owner: -
--

CREATE TABLE transaction_detail (
    transaction_id bigint NOT NULL,
    value text,
    key text NOT NULL
);


--
-- Name: blackbox_pkey; Type: CONSTRAINT; Schema: iovation; Owner: -
--

ALTER TABLE ONLY blackbox
    ADD CONSTRAINT blackbox_pkey PRIMARY KEY (id);


--
-- Name: transaction_detail_pkey; Type: CONSTRAINT; Schema: iovation; Owner: -
--

ALTER TABLE ONLY transaction_detail
    ADD CONSTRAINT transaction_detail_pkey PRIMARY KEY (transaction_id, key);


--
-- Name: transaction_pkey; Type: CONSTRAINT; Schema: iovation; Owner: -
--

ALTER TABLE ONLY transaction
    ADD CONSTRAINT transaction_pkey PRIMARY KEY (id);


ALTER TABLE iovation.transaction_detail ADD CONSTRAINT fk_transaction_detail_transaction_id FOREIGN KEY (transaction_id) REFERENCES iovation.transaction (id);
CREATE INDEX IF NOT EXISTS idx_transaction_detail_transaction_id ON iovation.transaction_detail USING btree (transaction_id);

