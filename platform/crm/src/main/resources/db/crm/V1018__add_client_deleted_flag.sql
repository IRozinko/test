ALTER TABLE crm.client
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE crm.client_audit
    ADD COLUMN deleted BOOLEAN;

DELETE
FROM crm.email_login
WHERE email like 'soft_deleted_%';

UPDATE crm.client
SET deleted = TRUE
WHERE first_name like 'soft_deleted_%';
