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
-- Name: security; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS security;


SET search_path = security, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: permission; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE permission (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    name text NOT NULL
);


--
-- Name: permission_audit; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE permission_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    name text
);


--
-- Name: role; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE role (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    name text NOT NULL
);


--
-- Name: role_audit; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE role_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    name text
);


--
-- Name: role_permission; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE role_permission (
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL
);


--
-- Name: role_permission_audit; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE role_permission_audit (
    rev integer NOT NULL,
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL,
    revtype smallint
);


--
-- Name: user; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE "user" (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    email text NOT NULL,
    password text NOT NULL,
    temporary_password boolean NOT NULL
);


--
-- Name: user_audit; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE user_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    email text,
    password text,
    temporary_password boolean
);


--
-- Name: user_role; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


--
-- Name: user_role_audit; Type: TABLE; Schema: security; Owner: -
--

CREATE TABLE user_role_audit (
    rev integer NOT NULL,
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    revtype smallint
);


--
-- Name: permission_audit_pkey; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY permission_audit
    ADD CONSTRAINT permission_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: permission_pkey; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY permission
    ADD CONSTRAINT permission_pkey PRIMARY KEY (id);


--
-- Name: role_audit_pkey; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY role_audit
    ADD CONSTRAINT role_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: role_permission_audit_pkey; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY role_permission_audit
    ADD CONSTRAINT role_permission_audit_pkey PRIMARY KEY (rev, role_id, permission_id);


--
-- Name: role_pkey; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- Name: uk_2ojme20jpga3r4r79tdso17gi; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY permission
    ADD CONSTRAINT uk_2ojme20jpga3r4r79tdso17gi UNIQUE (name);


--
-- Name: uk_8sewwnpamngi6b1dwaa88askk; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY role
    ADD CONSTRAINT uk_8sewwnpamngi6b1dwaa88askk UNIQUE (name);


--
-- Name: uk_ob8kqyqqgmefl0aco34akdtpe; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT uk_ob8kqyqqgmefl0aco34akdtpe UNIQUE (email);


--
-- Name: user_audit_pkey; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY user_audit
    ADD CONSTRAINT user_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: user_pkey; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY "user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: user_role_audit_pkey; Type: CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY user_role_audit
    ADD CONSTRAINT user_role_audit_pkey PRIMARY KEY (rev, user_id, role_id);


--
-- Name: fk8r7vxwlbm20i7nqloyf4t2qur; Type: FK CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY role_audit
    ADD CONSTRAINT fk8r7vxwlbm20i7nqloyf4t2qur FOREIGN KEY (rev) REFERENCES common.revision(id);



--
-- Name: fkak13qpfasjs1wx4hxqxc61kqh; Type: FK CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY permission_audit
    ADD CONSTRAINT fkak13qpfasjs1wx4hxqxc61kqh FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkeac4cni0mcsd2xg97sd58xodq; Type: FK CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY user_role_audit
    ADD CONSTRAINT fkeac4cni0mcsd2xg97sd58xodq FOREIGN KEY (rev) REFERENCES common.revision(id);



--
-- Name: fkhhpe81kjkt7f1ub0ee6fx73dl; Type: FK CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY role_permission_audit
    ADD CONSTRAINT fkhhpe81kjkt7f1ub0ee6fx73dl FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkl7cgyxmql1b59h0204xp62ki2; Type: FK CONSTRAINT; Schema: security; Owner: -
--

ALTER TABLE ONLY user_audit
    ADD CONSTRAINT fkl7cgyxmql1b59h0204xp62ki2 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

ALTER TABLE security.user_role ADD CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES security.user (id);
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON security.user_role USING btree (user_id);

ALTER TABLE security.user_role ADD CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES security.role (id);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON security.user_role USING btree (role_id);

ALTER TABLE security.role_permission ADD CONSTRAINT fk_role_permission_role_id FOREIGN KEY (role_id) REFERENCES security.role (id);
CREATE INDEX IF NOT EXISTS idx_role_permission_role_id ON security.role_permission USING btree (role_id);

ALTER TABLE security.role_permission ADD CONSTRAINT fk_role_permission_permission_id FOREIGN KEY (permission_id) REFERENCES security.permission (id);
CREATE INDEX IF NOT EXISTS idx_role_permission_permission_id ON security.role_permission USING btree (permission_id);
