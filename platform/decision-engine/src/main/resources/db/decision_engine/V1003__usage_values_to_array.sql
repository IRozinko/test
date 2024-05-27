create table decision_engine.scoring_value_usage_temp
(
    id                         bigint                      NOT NULL,
    created_at                 timestamp without time zone NOT NULL,
    created_by                 text,
    entity_version             bigint                      NOT NULL,
    updated_at                 timestamp without time zone NOT NULL,
    updated_by                 text,
    decision_engine_request_id bigint                      not null,
    scoring_keys               text[]                      not null,
    PRIMARY KEY (id)
);

insert into decision_engine.scoring_value_usage_temp
(id, created_at, created_by, entity_version, updated_at, updated_by, decision_engine_request_id, scoring_keys)

    (select min(id),
            min(created_at),
            min(created_by),
            min(entity_version),
            min(updated_at),
            min(updated_by),
            decision_engine_request_id,
            array_agg((select key from scoring_values.scoring_value where id = scoring_value_id))
     from decision_engine.scoring_value_usage
     group by decision_engine_request_id
    );

drop table decision_engine.scoring_value_usage;
alter table decision_engine.scoring_value_usage_temp
    rename to scoring_value_usage;

drop table scoring_values.scoring_value;








