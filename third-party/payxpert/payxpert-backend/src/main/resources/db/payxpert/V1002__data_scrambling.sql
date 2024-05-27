INSERT INTO common.data_scrambling(table_schema, table_name, column_name, type)
VALUES ('payxpert', 'credit_card', 'card_brand', 'CREDIT_CARD_TYPE'),
       ('payxpert', 'credit_card', 'card_expire_month', 'MONTH'),
       ('payxpert', 'credit_card', 'card_expire_year', 'YEAR'),
       ('payxpert', 'credit_card', 'card_holder_name', 'FULL_NAME'),
       ('payxpert', 'credit_card', 'card_number', 'CREDIT_CARD_NUMBER'),
       ('payxpert', 'payment_request', 'card_brand', 'CREDIT_CARD_TYPE'),
       ('payxpert', 'payment_request', 'card_expire_month', 'MONTH'),
       ('payxpert', 'payment_request', 'card_expire_year', 'YEAR'),
       ('payxpert', 'payment_request', 'card_holder_name', 'FULL_NAME'),
       ('payxpert', 'payment_request', 'card_number', 'CREDIT_CARD_NUMBER');
