CREATE TABLE activity.activity (
  id             INT8                     NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by     TEXT,
  entity_version INT8                     NOT NULL,
  updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by     TEXT,
  action         TEXT                     NOT NULL,
  agent          TEXT,
  application_id INT8,
  client_id      INT8                     NOT NULL,
  comments       TEXT,
  debt_action_id INT8,
  debt_id        INT8,
  details        TEXT,
  loan_id        INT8,
  payment_id     INT8,
  resolution     TEXT,
  source         TEXT,
  task_id        INT8,
  topic          TEXT,
  PRIMARY KEY (id)
);
