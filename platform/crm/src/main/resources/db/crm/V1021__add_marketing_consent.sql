CREATE TABLE crm.marketing_consent_log
(
    id             bigint                      NOT NULL,
    created_at     timestamp without time zone NOT NULL,
    created_by     text,
    entity_version bigint                      NOT NULL,
    updated_at     timestamp without time zone NOT NULL,
    updated_by     text,

    client_id      bigint                      NOT NULL,
    timestamp      timestamp with time zone    NOT NULL,
    value          boolean                     NOT NULL,
    source         text,
    note           text
);

CREATE INDEX IF NOT EXISTS idx_marketing_consent_log_client_id
    ON crm.marketing_consent_log USING btree (client_id);
