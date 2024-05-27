CREATE SCHEMA IF NOT EXISTS ga;

CREATE TABLE ga.client_data (
    id BIGINT NOT NULL,
    created_at timestamp with time zone NOT NULL,
    created_by TEXT,
    entity_version BIGINT NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    updated_by TEXT,
    client_id BIGINT NOT NULL,
    cookie_user_id TEXT,
    user_agent TEXT
);

ALTER TABLE ONLY ga.client_data ADD CONSTRAINT client_data_pkey PRIMARY KEY (id);
CREATE UNIQUE INDEX idx_client_data_client_id ON ga.client_data USING btree (client_id);

CREATE TABLE ga.client_data_audit (
    id BIGINT,
    rev           INTEGER,
    revtype       SMALLINT,
    created_at timestamp with time zone,
    created_by TEXT,
    entity_version BIGINT,
    updated_at timestamp with time zone,
    updated_by TEXT,
    client_id BIGINT,
    cookie_user_id TEXT,
    user_agent TEXT,
    PRIMARY KEY (id, rev)
);


CREATE TABLE ga.requests_log (
    id BIGINT NOT NULL,
    created_at timestamp with time zone NOT NULL,
    created_by TEXT,
    entity_version BIGINT NOT NULL,
    updated_at timestamp with time zone NOT NULL,
    updated_by TEXT,
    client_id BIGINT NOT NULL,
    request TEXT,
    response TEXT,
    response_code integer NOT NULL
);

ALTER TABLE ONLY ga.requests_log ADD CONSTRAINT requests_log_pkey PRIMARY KEY (id);

CREATE INDEX idx_requests_log_client_id ON ga.requests_log USING btree (client_id);


