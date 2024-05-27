INSERT INTO common.data_scrambling(table_schema, table_name, column_name, type)
VALUES ('payment', 'payment', 'details', 'SENTENCE'),
       ('payment', 'payment', 'key', 'GENERIC_KEY'),
       ('payment', 'statement_row', 'description', 'SENTENCE'),
       ('payment', 'statement_row', 'key', 'GENERIC_KEY'),
       ('payment', 'statement_row', 'reference', 'NULL_TEXT'),
       ('payment', 'statement_row', 'source_json', 'NULL_TEXT'),
       ('payment', 'statement_row', 'status_message', 'NULL_TEXT'),
       ('payment', 'statement_row', 'counterparty_account', 'NULL_TEXT'),
       ('payment', 'statement_row', 'counterparty_address', 'NULL_TEXT'),
       ('payment', 'statement_row', 'counterparty_name', 'NULL_TEXT');
