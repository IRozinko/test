INSERT INTO common.data_scrambling(table_schema, table_name, type)
VALUES ('crm', 'reset_password_token', 'TRUNCATE');

INSERT INTO common.data_scrambling(table_schema, table_name, column_name, type)
VALUES ('crm', 'client', 'account_number', 'IBAN'),
       ('crm', 'client', 'date_of_birth', 'BIRTHDAY'),
       ('crm', 'client', 'document_number', 'DOCUMENT_NUMBER'),
       ('crm', 'client', 'first_name', 'FIRST_NAME'),
       ('crm', 'client', 'gender', 'GENDER'),
       ('crm', 'client', 'last_name', 'LAST_NAME'),
       ('crm', 'client', 'maiden_name', 'NULL_TEXT'),
       ('crm', 'client', 'phone', 'PHONE_NUMBER'),
       ('crm', 'client', 'second_first_name', 'NULL_TEXT'),
       ('crm', 'client', 'second_last_name', 'NULL_TEXT'),
       ('crm', 'client_address', 'city', 'CITY'),
       ('crm', 'client_address', 'house_number', 'NULL_TEXT'),
       ('crm', 'client_address', 'postal_code', 'POSTAL_CODE'),
       ('crm', 'client_address', 'province', 'PROVINCE'),
       ('crm', 'client_address', 'street', 'STREET'),
       ('crm', 'client_attribute', 'value', 'SENTENCE'),
       ('crm', 'client_bank_account', 'account_number', 'IBAN'),
       ('crm', 'client_bank_account', 'account_owner_name', 'FULL_NAME'),
       ('crm', 'client_bank_account', 'balance', 'BALANCE'),
       ('crm', 'client_bank_account', 'bank_name', 'COMPANY'),
       ('crm', 'client_bank_account', 'number_of_transactions', 'NUMBER'),
       ('crm', 'email_contact', 'email', 'EMAIL'),
       ('crm', 'email_login', 'email', 'EMAIL'),
       ('crm', 'identity_document', 'number', 'DOCUMENT_NUMBER'),
       ('crm', 'phone_contact', 'local_number', 'PHONE_NUMBER');
