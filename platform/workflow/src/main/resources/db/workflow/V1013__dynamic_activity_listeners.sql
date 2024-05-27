CREATE TABLE workflow.activity_listener
(
    id               BIGINT                   NOT NULL PRIMARY KEY,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by       TEXT,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by       TEXT,
    entity_version   BIGINT                   NOT NULL,
    name             TEXT                     NOT NULL,
    workflow_name    TEXT                     NOT NULL,
    workflow_version INTEGER                  NOT NULL,
    activity_name    TEXT                     NOT NULL,
    trigger_name     TEXT                     NOT NULL,
    activity_status  TEXT                     NOT NULL,
    resolution       TEXT,
    delay_sec        INTEGER,
    from_midnight    boolean,
    params           TEXT[]
);

create index idx_activity_listener_trigger_activity_started
    on workflow.activity_listener (workflow_name, workflow_version, trigger_name, activity_name) where activity_status = 'STARTED';

create index idx_activity_listener_trigger_activity_resolution_completed
    on workflow.activity_listener (workflow_name, workflow_version, trigger_name, activity_name,
                                   resolution) where activity_status = 'COMPLETED';

create table workflow.activity_listener_audit
(
    id               bigint  NOT NULL,
    rev              INTEGER NOT NULL,
    revtype          SMALLINT,
    created_at       timestamp with time zone,
    created_by       TEXT,
    updated_at       timestamp with time zone,
    updated_by       TEXT,
    name             TEXT    NOT NULL,
    workflow_name    TEXT    NOT NULL,
    workflow_version INTEGER NOT NULL,
    activity_name    TEXT    NOT NULL,
    trigger_name     TEXT    NOT NULL,
    activity_status  TEXT    NOT NULL,
    resolution       TEXT,
    delay_sec        INTEGER,
    from_midnight    boolean,
    params           TEXT[]
);


