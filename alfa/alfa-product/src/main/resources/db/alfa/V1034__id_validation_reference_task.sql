SET search_path = alfa;

ALTER TABLE alfa.identification_document
    ADD COLUMN task_id bigint;

ALTER TABLE alfa.identification_document_audit
    ADD COLUMN task_id bigint;

CREATE INDEX idx_alfa_identification_doc_task_id
    ON alfa.identification_document (task_id);

ALTER TABLE identification_document
    ADD CONSTRAINT fk_identification_doc_task FOREIGN KEY (task_id) REFERENCES task.task (id);
