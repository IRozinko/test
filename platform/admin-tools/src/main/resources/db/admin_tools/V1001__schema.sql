CREATE TABLE admin_tools.log (
  id             INT8                     NOT NULL,
  created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  created_by     TEXT,
  entity_version INT8                     NOT NULL,
  updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_by     TEXT,
  error          TEXT,
  params         TEXT,
  message        TEXT,
  name           TEXT                     NOT NULL,
  status         TEXT                     NOT NULL,
  PRIMARY KEY (id)
);
