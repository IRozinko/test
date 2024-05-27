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
-- Name: common; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS common;


SET search_path = common, pg_catalog;

--
-- Name: id_seq; Type: SEQUENCE; Schema: common; Owner: -
--

CREATE SEQUENCE id_seq
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: rev_id_seq; Type: SEQUENCE; Schema: common; Owner: -
--

CREATE SEQUENCE rev_id_seq
    START WITH 10000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: revision; Type: TABLE; Schema: common; Owner: -
--

CREATE TABLE revision (
    id integer NOT NULL,
    ip_address text,
    request_id text,
    request_uri text,
    revision_at timestamp without time zone,
    user_name text NOT NULL
);


--
-- Name: revision_pkey; Type: CONSTRAINT; Schema: common; Owner: -
--

ALTER TABLE ONLY revision
    ADD CONSTRAINT revision_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

