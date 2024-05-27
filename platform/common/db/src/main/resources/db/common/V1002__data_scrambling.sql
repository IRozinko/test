CREATE TABLE common.data_scrambling
(
    table_schema TEXT    NOT NULL,
    table_name   TEXT    NOT NULL,
    column_name  TEXT,
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    type         TEXT    NOT NULL,
    CONSTRAINT data_scrambling_unique
        UNIQUE (table_schema, table_name, column_name)
);
