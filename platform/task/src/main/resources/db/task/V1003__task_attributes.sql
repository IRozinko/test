CREATE TABLE task.task_attribute (
  task_id INT8 NOT NULL,
  value   TEXT,
  key     TEXT NOT NULL,
  PRIMARY KEY (task_id, key)
);

CREATE TABLE task.task_attribute_audit (
  rev     INT4 NOT NULL,
  task_id INT8 NOT NULL,
  value   TEXT NOT NULL,
  key     TEXT NOT NULL,
  revtype INT2,
  PRIMARY KEY (rev, task_id, value, key)
);

ALTER TABLE task.task_attribute
  ADD CONSTRAINT FKkkpewueys70bcx84x9yd2dsec
FOREIGN KEY (task_id)
REFERENCES task.task;

ALTER TABLE task.task_attribute_audit
  ADD CONSTRAINT FKhq6j0l3euo3lol2yvw0opip5x
FOREIGN KEY (rev)
REFERENCES common.revision;
