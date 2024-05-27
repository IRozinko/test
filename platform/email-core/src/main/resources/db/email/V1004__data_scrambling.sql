INSERT INTO common.data_scrambling(table_schema, table_name, column_name, type)
VALUES ('email', 'log', 'body', 'SENTENCE'),
       ('email', 'log', 'subject', 'SENTENCE'),
       ('email', 'log', 'send_to', 'EMAIL');
