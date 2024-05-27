INSERT INTO common.data_scrambling(table_schema, table_name, type)
VALUES ('sms', 'incoming', 'TRUNCATE');

INSERT INTO common.data_scrambling(table_schema, table_name, column_name, type)
VALUES ('sms', 'log', 'sms_text', 'SENTENCE'),
       ('sms', 'log', 'send_to', 'PHONE_NUMBER');
