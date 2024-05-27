CREATE TABLE instantor.account (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    account_holder_name text,
    account_number text NOT NULL,
    balance numeric(19,2),
    client_id bigint,
    currency text NOT NULL,
    response_id bigint NOT NULL
);

ALTER TABLE ONLY instantor.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


ALTER TABLE instantor.account ADD CONSTRAINT fk_account_response_id FOREIGN KEY (response_id) REFERENCES instantor.response (id);
CREATE INDEX IF NOT EXISTS idx_account_response_id ON instantor.account USING btree (response_id);

ALTER TABLE instantor.transaction ADD COLUMN account_id bigint;
ALTER TABLE instantor.transaction_audit ADD COLUMN account_id bigint;
