INSERT INTO common.data_scrambling(table_schema, table_name, type)
VALUES ('workflow', 'scoring_value', 'TRUNCATE');

INSERT INTO common.data_scrambling(table_schema, table_name, column_name, type)
VALUES ('workflow', 'workflow_attribute', 'value', 'SENTENCE');
