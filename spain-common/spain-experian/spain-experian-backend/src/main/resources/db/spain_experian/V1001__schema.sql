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
-- Name: spain_experian; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS spain_experian;


SET search_path = spain_experian, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: cais_operaciones; Type: TABLE; Schema: spain_experian; Owner: -
--

CREATE TABLE cais_operaciones (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    client_id bigint,
    document_number text NOT NULL,
    error text,
    numero_registros_devueltos integer,
    request_body text,
    response_body text,
    status text NOT NULL
);


--
-- Name: cais_resumen; Type: TABLE; Schema: spain_experian; Owner: -
--

CREATE TABLE cais_resumen (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    client_id bigint,
    document_number text NOT NULL,
    error text,
    importe_total_impagado numeric(19,2),
    maximo_importe_impagado numeric(19,2),
    numero_total_cuotas_impagadas integer,
    numero_total_operaciones_impagadas integer,
    peor_situacion_pago text,
    peor_situacion_pago_historica text,
    provincia_codigo text,
    request_body text,
    response_body text,
    status text NOT NULL
);


--
-- Name: cais_operaciones_pkey; Type: CONSTRAINT; Schema: spain_experian; Owner: -
--

ALTER TABLE ONLY cais_operaciones
    ADD CONSTRAINT cais_operaciones_pkey PRIMARY KEY (id);


--
-- Name: cais_resumen_pkey; Type: CONSTRAINT; Schema: spain_experian; Owner: -
--

ALTER TABLE ONLY cais_resumen
    ADD CONSTRAINT cais_resumen_pkey PRIMARY KEY (id);


--
-- Name: idx_cais_operaciones_client_id; Type: INDEX; Schema: spain_experian; Owner: -
--

CREATE INDEX idx_cais_operaciones_client_id ON cais_operaciones USING btree (client_id);


--
-- Name: idx_cais_resumen_client_id; Type: INDEX; Schema: spain_experian; Owner: -
--

CREATE INDEX idx_cais_resumen_client_id ON cais_resumen USING btree (client_id);


--
-- PostgreSQL database dump complete
--

