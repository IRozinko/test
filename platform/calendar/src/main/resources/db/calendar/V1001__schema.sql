CREATE TABLE calendar.holiday (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    date date NOT NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_unique_holiday_data ON calendar.holiday (date);

CREATE TABLE calendar.holiday_audit (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    created_at timestamp without time zone,
    created_by text,
    updated_at timestamp without time zone,
    updated_by text,
    date date,
    PRIMARY KEY(id, rev)
);
