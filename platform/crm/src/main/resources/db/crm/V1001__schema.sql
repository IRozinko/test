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
-- Name: crm; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS crm;


SET search_path = crm, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: client; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    accept_marketing boolean NOT NULL,
    accept_terms boolean NOT NULL,
    accept_verification boolean NOT NULL,
    account_number text,
    date_of_birth date,
    document_number text,
    email text,
    first_name text,
    gender text,
    last_name text,
    maiden_name text,
    client_number text NOT NULL,
    phone text,
    second_first_name text,
    second_last_name text,
    segments_text text
);


--
-- Name: client_address; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_address (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    city text,
    house_number text,
    housing_tenure text,
    postal_code text,
    province text,
    street text,
    type text NOT NULL,
    client_id bigint NOT NULL
);


--
-- Name: client_address_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_address_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    city text,
    house_number text,
    housing_tenure text,
    postal_code text,
    province text,
    street text,
    type text,
    client_id bigint
);


--
-- Name: client_attachment; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_attachment (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    application_id bigint,
    file_id bigint NOT NULL,
    attachment_group text NOT NULL,
    loan_id bigint,
    name text,
    status text,
    status_detail text,
    attachment_type text NOT NULL,
    client_id bigint NOT NULL
);


--
-- Name: client_attachment_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_attachment_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    application_id bigint,
    file_id bigint,
    attachment_group text,
    loan_id bigint,
    name text,
    status text,
    status_detail text,
    attachment_type text,
    client_id bigint
);


--
-- Name: client_attribute; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_attribute (
    client_id bigint NOT NULL,
    value text,
    key text NOT NULL
);


--
-- Name: client_attribute_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_attribute_audit (
    rev integer NOT NULL,
    client_id bigint NOT NULL,
    value text NOT NULL,
    key text NOT NULL,
    revtype smallint
);


--
-- Name: client_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    accept_marketing boolean,
    accept_terms boolean,
    accept_verification boolean,
    account_number text,
    date_of_birth date,
    document_number text,
    email text,
    first_name text,
    gender text,
    last_name text,
    maiden_name text,
    client_number text,
    phone text,
    second_first_name text,
    second_last_name text,
    segments_text text
);


--
-- Name: client_bank_account; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_bank_account (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    account_number text NOT NULL,
    account_owner_name text NOT NULL,
    balance numeric(19,2),
    bank_name text NOT NULL,
    currency text,
    is_primary boolean NOT NULL,
    client_id bigint NOT NULL
);


--
-- Name: client_bank_account_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_bank_account_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    account_number text,
    account_owner_name text,
    balance numeric(19,2),
    bank_name text,
    currency text,
    is_primary boolean,
    client_id bigint
);


--
-- Name: client_segment; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_segment (
    client_id bigint NOT NULL,
    added_at timestamp without time zone NOT NULL,
    segment text NOT NULL
);


--
-- Name: client_segment_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE client_segment_audit (
    rev integer NOT NULL,
    revtype smallint NOT NULL,
    client_id bigint NOT NULL,
    setordinal integer NOT NULL,
    added_at timestamp without time zone,
    segment text
);


--
-- Name: email_contact; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE email_contact (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    email text NOT NULL,
    is_primary boolean NOT NULL,
    client_id bigint NOT NULL
);


--
-- Name: email_contact_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE email_contact_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    email text,
    is_primary boolean,
    client_id bigint
);


--
-- Name: email_login; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE email_login (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    email text NOT NULL,
    password text NOT NULL,
    client_id bigint NOT NULL
);


--
-- Name: email_login_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE email_login_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    email text,
    password text,
    client_id bigint
);


--
-- Name: identity_document; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE identity_document (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    expires_at date,
    issued_at date,
    issued_by text,
    number text NOT NULL,
    is_primary boolean NOT NULL,
    document_type text NOT NULL,
    client_id bigint NOT NULL
);


--
-- Name: identity_document_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE identity_document_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    expires_at date,
    issued_at date,
    issued_by text,
    number text,
    is_primary boolean,
    document_type text,
    client_id bigint
);


--
-- Name: phone_contact; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE phone_contact (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    country_code text NOT NULL,
    local_number text NOT NULL,
    phone_type integer NOT NULL,
    is_primary boolean NOT NULL,
    verified boolean NOT NULL,
    verified_at timestamp without time zone,
    client_id bigint NOT NULL
);


--
-- Name: phone_contact_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE phone_contact_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    country_code text,
    local_number text,
    phone_type integer,
    is_primary boolean,
    verified boolean,
    verified_at timestamp without time zone,
    client_id bigint
);


--
-- Name: phone_verification; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE phone_verification (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    code text NOT NULL,
    latest boolean NOT NULL,
    verified boolean NOT NULL,
    verified_at timestamp without time zone,
    client_id bigint NOT NULL,
    phone_contact_id bigint NOT NULL
);


--
-- Name: phone_verification_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE phone_verification_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    code text,
    latest boolean,
    verified boolean,
    verified_at timestamp without time zone,
    client_id bigint,
    phone_contact_id bigint
);


--
-- Name: reset_password_token; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE reset_password_token (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    expires_at timestamp without time zone NOT NULL,
    token text NOT NULL,
    is_used boolean NOT NULL,
    client_id bigint NOT NULL
);


--
-- Name: reset_password_token_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE reset_password_token_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    expires_at timestamp without time zone,
    token text,
    is_used boolean,
    client_id bigint
);


--
-- Name: verify_email_token; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE verify_email_token (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    expires_at timestamp without time zone NOT NULL,
    token text NOT NULL,
    is_used boolean NOT NULL,
    client_id bigint NOT NULL
);


--
-- Name: verify_email_token_audit; Type: TABLE; Schema: crm; Owner: -
--

CREATE TABLE verify_email_token_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    expires_at timestamp without time zone,
    token text,
    is_used boolean,
    client_id bigint
);


--
-- Name: client_address_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_address_audit
    ADD CONSTRAINT client_address_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: client_address_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_address
    ADD CONSTRAINT client_address_pkey PRIMARY KEY (id);


--
-- Name: client_attachment_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_attachment_audit
    ADD CONSTRAINT client_attachment_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: client_attachment_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_attachment
    ADD CONSTRAINT client_attachment_pkey PRIMARY KEY (id);


--
-- Name: client_attribute_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_attribute_audit
    ADD CONSTRAINT client_attribute_audit_pkey PRIMARY KEY (rev, client_id, value, key);


--
-- Name: client_attribute_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_attribute
    ADD CONSTRAINT client_attribute_pkey PRIMARY KEY (client_id, key);


--
-- Name: client_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_audit
    ADD CONSTRAINT client_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: client_bank_account_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_bank_account_audit
    ADD CONSTRAINT client_bank_account_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: client_bank_account_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_bank_account
    ADD CONSTRAINT client_bank_account_pkey PRIMARY KEY (id);


--
-- Name: client_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client
    ADD CONSTRAINT client_pkey PRIMARY KEY (id);


--
-- Name: client_segment_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_segment_audit
    ADD CONSTRAINT client_segment_audit_pkey PRIMARY KEY (rev, revtype, client_id, setordinal);


--
-- Name: client_segment_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_segment
    ADD CONSTRAINT client_segment_pkey PRIMARY KEY (client_id, added_at, segment);


--
-- Name: email_contact_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY email_contact_audit
    ADD CONSTRAINT email_contact_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: email_contact_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY email_contact
    ADD CONSTRAINT email_contact_pkey PRIMARY KEY (id);


--
-- Name: email_login_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY email_login_audit
    ADD CONSTRAINT email_login_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: email_login_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY email_login
    ADD CONSTRAINT email_login_pkey PRIMARY KEY (id);


--
-- Name: identity_document_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY identity_document_audit
    ADD CONSTRAINT identity_document_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: identity_document_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY identity_document
    ADD CONSTRAINT identity_document_pkey PRIMARY KEY (id);


--
-- Name: idx_email_login_client_id; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY email_login
    ADD CONSTRAINT idx_email_login_client_id UNIQUE (client_id);


--
-- Name: phone_contact_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY phone_contact_audit
    ADD CONSTRAINT phone_contact_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: phone_contact_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY phone_contact
    ADD CONSTRAINT phone_contact_pkey PRIMARY KEY (id);


--
-- Name: phone_verification_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY phone_verification_audit
    ADD CONSTRAINT phone_verification_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: phone_verification_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY phone_verification
    ADD CONSTRAINT phone_verification_pkey PRIMARY KEY (id);


--
-- Name: reset_password_token_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY reset_password_token_audit
    ADD CONSTRAINT reset_password_token_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: reset_password_token_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY reset_password_token
    ADD CONSTRAINT reset_password_token_pkey PRIMARY KEY (id);


--
-- Name: uk_8nip4eqckvxu35ab09a8ci3ef; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY email_login
    ADD CONSTRAINT uk_8nip4eqckvxu35ab09a8ci3ef UNIQUE (email);


--
-- Name: uk_fnj2pbpgsymh6j0tq0vq3q69v; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY verify_email_token
    ADD CONSTRAINT uk_fnj2pbpgsymh6j0tq0vq3q69v UNIQUE (token);


--
-- Name: uk_m1q62so82atxntbpu9nrwu9o0; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY reset_password_token
    ADD CONSTRAINT uk_m1q62so82atxntbpu9nrwu9o0 UNIQUE (token);


--
-- Name: uk_nd3iv3viwo4nl1n6wq5wsjafq; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client
    ADD CONSTRAINT uk_nd3iv3viwo4nl1n6wq5wsjafq UNIQUE (client_number);


--
-- Name: verify_email_token_audit_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY verify_email_token_audit
    ADD CONSTRAINT verify_email_token_audit_pkey PRIMARY KEY (id, rev);


--
-- Name: verify_email_token_pkey; Type: CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY verify_email_token
    ADD CONSTRAINT verify_email_token_pkey PRIMARY KEY (id);


--
-- Name: idx_client_address_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_client_address_client_id ON client_address USING btree (client_id);


--
-- Name: idx_client_attachment_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_client_attachment_client_id ON client_attachment USING btree (client_id);


--
-- Name: idx_client_bank_account_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_client_bank_account_client_id ON client_bank_account USING btree (client_id);


--
-- Name: idx_email_contact_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_email_contact_client_id ON email_contact USING btree (client_id);


--
-- Name: idx_identity_document_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_identity_document_client_id ON identity_document USING btree (client_id);


--
-- Name: idx_phone_contact_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_phone_contact_client_id ON phone_contact USING btree (client_id);


--
-- Name: idx_phone_verification_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_phone_verification_client_id ON phone_verification USING btree (client_id);


--
-- Name: idx_reset_password_token_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_reset_password_token_client_id ON reset_password_token USING btree (client_id);


--
-- Name: idx_verify_email_token_client_id; Type: INDEX; Schema: crm; Owner: -
--

CREATE INDEX idx_verify_email_token_client_id ON verify_email_token USING btree (client_id);

--
-- Name: fk1dd9qr0g7m1yvkde69huyefjc; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_attachment_audit
    ADD CONSTRAINT fk1dd9qr0g7m1yvkde69huyefjc FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fk2acxba9n8mg9ds91bwtytahvs; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY reset_password_token_audit
    ADD CONSTRAINT fk2acxba9n8mg9ds91bwtytahvs FOREIGN KEY (rev) REFERENCES common.revision(id);

--
-- Name: fk6m255q3bfh1fy15rth2mt3g4f; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY identity_document_audit
    ADD CONSTRAINT fk6m255q3bfh1fy15rth2mt3g4f FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkaddjk6g56hjyv64cijhdr4m6j; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_segment_audit
    ADD CONSTRAINT fkaddjk6g56hjyv64cijhdr4m6j FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkb5uw5ddunsvlfpbh9q3695aj6; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY phone_contact_audit
    ADD CONSTRAINT fkb5uw5ddunsvlfpbh9q3695aj6 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkbpe4d0s2kjwtfwtfwriyr9834; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY email_contact_audit
    ADD CONSTRAINT fkbpe4d0s2kjwtfwtfwriyr9834 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkepan9ij26j1vgsxbbvcvc4mm5; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_bank_account_audit
    ADD CONSTRAINT fkepan9ij26j1vgsxbbvcvc4mm5 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkf4grlbehjhcs0wa7crey2cre5; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY verify_email_token_audit
    ADD CONSTRAINT fkf4grlbehjhcs0wa7crey2cre5 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fklsn5k7cxapbjo2cw41ghur6rj; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY email_login_audit
    ADD CONSTRAINT fklsn5k7cxapbjo2cw41ghur6rj FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkm0k4wp8qra4igvmoyo7d838ag; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY phone_verification_audit
    ADD CONSTRAINT fkm0k4wp8qra4igvmoyo7d838ag FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkm5qhsl05ng0tsdttk8n040pr8; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_address_audit
    ADD CONSTRAINT fkm5qhsl05ng0tsdttk8n040pr8 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkmdp6sqxxvtxksty8ytjcpfr5c; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_audit
    ADD CONSTRAINT fkmdp6sqxxvtxksty8ytjcpfr5c FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- Name: fkrx3psfh9y2r2uug3cd4hhfvf6; Type: FK CONSTRAINT; Schema: crm; Owner: -
--

ALTER TABLE ONLY client_attribute_audit
    ADD CONSTRAINT fkrx3psfh9y2r2uug3cd4hhfvf6 FOREIGN KEY (rev) REFERENCES common.revision(id);


--
-- PostgreSQL database dump complete
--

ALTER TABLE crm.verify_email_token ADD CONSTRAINT fk_verify_email_token_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_verify_email_token_client_id ON crm.verify_email_token USING btree (client_id);

ALTER TABLE crm.reset_password_token ADD CONSTRAINT fk_reset_password_token_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_reset_password_token_client_id ON crm.reset_password_token USING btree (client_id);

ALTER TABLE crm.phone_verification ADD CONSTRAINT fk_phone_verification_phone_contact_id FOREIGN KEY (phone_contact_id) REFERENCES crm.phone_contact (id);
CREATE INDEX IF NOT EXISTS idx_phone_verification_phone_contact_id ON crm.phone_verification USING btree (phone_contact_id);

ALTER TABLE crm.phone_verification ADD CONSTRAINT fk_phone_verification_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_phone_verification_client_id ON crm.phone_verification USING btree (client_id);

ALTER TABLE crm.phone_contact ADD CONSTRAINT fk_phone_contact_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_phone_contact_client_id ON crm.phone_contact USING btree (client_id);

ALTER TABLE crm.identity_document ADD CONSTRAINT fk_identity_document_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_identity_document_client_id ON crm.identity_document USING btree (client_id);

ALTER TABLE crm.email_login ADD CONSTRAINT fk_email_login_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_email_login_client_id ON crm.email_login USING btree (client_id);

ALTER TABLE crm.email_contact ADD CONSTRAINT fk_email_contact_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_email_contact_client_id ON crm.email_contact USING btree (client_id);

ALTER TABLE crm.client_segment ADD CONSTRAINT fk_client_segment_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_client_segment_client_id ON crm.client_segment USING btree (client_id);

ALTER TABLE crm.client_bank_account ADD CONSTRAINT fk_client_bank_account_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_client_bank_account_client_id ON crm.client_bank_account USING btree (client_id);

ALTER TABLE crm.client_attribute ADD CONSTRAINT fk_client_attribute_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_client_attribute_client_id ON crm.client_attribute USING btree (client_id);

ALTER TABLE crm.client_attachment ADD CONSTRAINT fk_client_attachment_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_client_attachment_client_id ON crm.client_attachment USING btree (client_id);

ALTER TABLE crm.client_address ADD CONSTRAINT fk_client_address_client_id FOREIGN KEY (client_id) REFERENCES crm.client (id);
CREATE INDEX IF NOT EXISTS idx_client_address_client_id ON crm.client_address USING btree (client_id);
