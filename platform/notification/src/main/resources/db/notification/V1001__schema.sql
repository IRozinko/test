SET client_encoding = 'UTF8';
SET search_path = notification, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = false;

CREATE TABLE notification (
    id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    created_by text,
    entity_version bigint NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    updated_by text,
    sms_log_id bigint,
    email_log_id bigint,
    cms_key text,
    sent_at timestamp without time zone NOT NULL,
    client_id bigint,
    loan_id bigint,
    loan_application_id bigint,
    task_id bigint,
    debt_id bigint
);

ALTER TABLE ONLY notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id),
    ADD CONSTRAINT fk_notification_sms_log_id FOREIGN KEY (sms_log_id) REFERENCES sms.log(id),
    ADD CONSTRAINT fk_notification_email_log_id FOREIGN KEY (email_log_id) REFERENCES email.log(id);

CREATE INDEX IF NOT EXISTS idx_notification_client_id ON notification USING btree (client_id);
CREATE INDEX IF NOT EXISTS idx_notification_loan_id ON notification USING btree (loan_id);
CREATE INDEX IF NOT EXISTS idx_notification_loan_application_id ON notification USING btree (loan_application_id);
CREATE INDEX IF NOT EXISTS idx_notification_task_id ON notification USING btree (task_id);
CREATE INDEX IF NOT EXISTS idx_notification_debt_id ON notification USING btree (debt_id);
CREATE INDEX IF NOT EXISTS idx_notification_email_log_id ON notification USING btree (email_log_id);
CREATE INDEX IF NOT EXISTS idx_notification_sms_log_id ON notification USING btree (sms_log_id);

INSERT INTO notification.notification(id, created_at, created_by, entity_version, updated_at, updated_by, sms_log_id, email_log_id, cms_key, sent_at, client_id, loan_id, loan_application_id, task_id, debt_id)
SELECT nextval('common.id_seq'), created_at, created_by, entity_version, updated_at, updated_by, null, id, cms_key, updated_at, client_id, loan_id, loan_application_id, task_id, debt_id
FROM email.log;

INSERT INTO notification.notification(id, created_at, created_by, entity_version, updated_at, updated_by, sms_log_id, email_log_id, cms_key, sent_at, client_id, loan_id, loan_application_id, task_id, debt_id)
SELECT nextval('common.id_seq'), created_at, created_by, entity_version, updated_at, updated_by, id, null, cms_key, updated_at, client_id, loan_id, loan_application_id, task_id, debt_id
FROM sms.log;
