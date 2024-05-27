CREATE SCHEMA IF NOT EXISTS decision_engine;

SET search_path = decision_engine;

CREATE TABLE request
(
    id               bigint                      NOT NULL,
    created_at       timestamp without time zone NOT NULL,
    created_by       text,
    entity_version   bigint                      NOT NULL,
    updated_at       timestamp without time zone NOT NULL,
    updated_by       text,

    scenario         text                        NOT NULL,
    client_id        bigint                      NOT NULL,
    scoring_model_id bigint                      NOT NULL,

    status           text,
    error            text,
    response         text,
    decision         text,
    rating           text,
    score            text,
    variables_result text,
    PRIMARY KEY (id)
);

CREATE TABLE scoring_value_usage
(
    id                         bigint                      NOT NULL,
    created_at                 timestamp without time zone NOT NULL,
    created_by                 text,
    entity_version             bigint                      NOT NULL,
    updated_at                 timestamp without time zone NOT NULL,
    updated_by                 text,

    decision_engine_request_id bigint                      not null,
    scoring_value_id           bigint                      not null,

    PRIMARY KEY (id),
    FOREIGN KEY (scoring_value_id) references scoring_values.scoring_value (id)
);















