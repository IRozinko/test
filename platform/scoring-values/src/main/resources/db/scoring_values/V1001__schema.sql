CREATE SCHEMA IF NOT EXISTS scoring_values;

SET search_path = scoring_values;

CREATE TABLE scoring_model
(
    id             bigint                      NOT NULL,
    created_at     timestamp without time zone NOT NULL,
    created_by     text,
    entity_version bigint                      NOT NULL,
    updated_at     timestamp without time zone NOT NULL,
    updated_by     text,

    client_id      bigint                      NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_scoring_model_client_id ON scoring_model (client_id);

CREATE TABLE scoring_value
(
    id             bigint                      NOT NULL,
    created_at     timestamp without time zone NOT NULL,
    created_by     text,
    entity_version bigint                      NOT NULL,
    updated_at     timestamp without time zone NOT NULL,
    updated_by     text,

    model_id       bigint                      NOT NULL,
    key            text                        NOT NULL,
    value          text,
    source         text                        NOT NULL,
    type           text,
    PRIMARY KEY (id),
    FOREIGN KEY (model_id) REFERENCES scoring_model (id)
);

CREATE INDEX idx_scoring_value_model_id ON scoring_value (model_id);














